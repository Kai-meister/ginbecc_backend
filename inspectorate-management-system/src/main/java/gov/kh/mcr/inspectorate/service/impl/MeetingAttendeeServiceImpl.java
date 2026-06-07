package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.AttendeeResponse;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
import gov.kh.mcr.inspectorate.enums.MeetingStatusCode;
import gov.kh.mcr.inspectorate.enums.NotificationType;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper.AttendeeMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MeetingAttendeeServiceImpl
        implements MeetingAttendeeService {

    private final MeetingAttendeeRepository attendeeRepo;
    private final MeetingRepository         meetingRepo;
    private final OfficerRepository         officerRepo;
    private final AttendeeMapper            attendeeMapper;
    private final SecurityUtils             securityUtils;
    private final NotificationService       notificationService;
    private final ActivityLogService        activityLogService;

    // ─────────────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<AttendeeResponse> getByMeetingId(
            Integer meetingId) {

        findMeeting(meetingId);

        return attendeeRepo
                .findByMeeting_MeetingId(meetingId)
                .stream()
                .map(attendeeMapper::toResponse)
                .toList();
    }

    // ─────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public AttendeeResponse getById(
            Integer meetingId,
            Integer attendeeId) {

        MeetingAttendee attendee =
                findById(attendeeId);

        if (!attendee.getMeeting()
                .getMeetingId()
                .equals(meetingId)) {
            throw new ResourceNotFoundException(
                    "Attendee", attendeeId);
        }

        return attendeeMapper.toResponse(attendee);
    }

    // ─────────────────────────────────────────────
    // ADD ONE ATTENDEE
    // ─────────────────────────────────────────────
    @Override
    public AttendeeResponse addAttendee(
            Integer meetingId,
            AttendeeRequest request) {

        Meeting meeting = findMeeting(meetingId);

        // Check meeting cancellable
        validateMeetingActive(meeting);

        // Check duplicate
        if (attendeeRepo
                .existsByMeeting_MeetingIdAndOfficer_OfficerId(
                        meetingId,
                        request.getOfficerId())) {
            throw new DuplicateResourceException(
                    "មន្ត្រីនេះ"
                            + " មានក្នុងបញ្ជីរួចហើយ");
        }

        Officer officer =
                findOfficer(request.getOfficerId());

        MeetingAttendee attendee =
                MeetingAttendee.builder()
                        .meeting(meeting)
                        .officer(officer)
                        .role(request.getRole())
                        .attendanceStatus(
                                AttendanceStatus.INVITED)
                        .build();

        MeetingAttendee saved =
                attendeeRepo.save(attendee);

        // Notify officer
        notificationService.createByOfficerId(
                officer.getOfficerId(),
                "ការអញ្ជើញប្រជុំ",
                "អញ្ជើញ: "
                        + meeting.getTitle()
                        + " ("
                        + meeting.getMeetingDate()
                        + ")",
                NotificationType.MEETING,
                meetingId);

        activityLogService.log(
                "CREATE", "MeetingAttendee",
                saved.getAttendeeId(),
                "Add: "
                        + officer.getFullNameKh()
                        + " → " + meeting.getTitle(),
                buildContext());

        return attendeeMapper.toResponse(saved);
    }

    // ─────────────────────────────────────────────
    // ADD BULK
    // ─────────────────────────────────────────────
    @Override
    public List<AttendeeResponse> addBulk(
            Integer meetingId,
            BulkAttendeeRequest request) {

        Meeting meeting = findMeeting(meetingId);
        validateMeetingActive(meeting);

        List<AttendeeResponse> results =
                new ArrayList<>();

        request.getOfficerIds()
                .forEach(officerId -> {

                    // Skip duplicate
                    if (attendeeRepo
                            .existsByMeeting_MeetingIdAndOfficer_OfficerId(
                                    meetingId, officerId)) {
                        log.warn(
                                "Duplicate officer {}"
                                        + " in meeting {}",
                                officerId, meetingId);
                        return;
                    }

                    officerRepo.findById(officerId)
                            .ifPresentOrElse(
                                    officer -> {
                                        MeetingAttendee a =
                                                MeetingAttendee.builder()
                                                        .meeting(meeting)
                                                        .officer(officer)
                                                        .role(request.getRole())
                                                        .attendanceStatus(
                                                                AttendanceStatus
                                                                        .INVITED)
                                                        .build();

                                        results.add(
                                                attendeeMapper.toResponse(
                                                        attendeeRepo.save(a)));

                                        // Notify
                                        notificationService
                                                .createByOfficerId(
                                                        officerId,
                                                        "ការអញ្ជើញប្រជុំ",
                                                        "អញ្ជើញ: "
                                                                + meeting.getTitle(),
                                                        NotificationType.MEETING,
                                                        meetingId);
                                    },
                                    () -> log.warn(
                                            "Officer {} not found",
                                            officerId));
                });

        activityLogService.log(
                "CREATE", "MeetingAttendee",
                meetingId,
                "Bulk add "
                        + results.size()
                        + " attendees",
                buildContext());

        return results;
    }

    // ─────────────────────────────────────────────
    // UPDATE ATTENDANCE STATUS
    // ─────────────────────────────────────────────
    @Override
    public AttendeeResponse updateAttendance(
            Integer meetingId,
            Integer attendeeId,
            AttendanceUpdateRequest request) {

        MeetingAttendee attendee =
                findById(attendeeId);

        if (!attendee.getMeeting()
                .getMeetingId()
                .equals(meetingId)) {
            throw new ResourceNotFoundException(
                    "Attendee", attendeeId);
        }

        attendee.setAttendanceStatus(
                request.getAttendanceStatus());

        if (request.getNote() != null) {
            attendee.setNote(request.getNote());
        }

        // Auto check-in time
        if (request.getAttendanceStatus()
                == AttendanceStatus.ATTENDED
                && attendee.getCheckInTime() == null) {
            attendee.setCheckInTime(
                    LocalDateTime.now());
        }

        MeetingAttendee saved =
                attendeeRepo.save(attendee);

        activityLogService.log(
                "UPDATE", "MeetingAttendee",
                attendeeId,
                "Attendance: "
                        + request.getAttendanceStatus()
                        .getLabelKh(),
                buildContext());

        return attendeeMapper.toResponse(saved);
    }

    // ─────────────────────────────────────────────
    // REMOVE ATTENDEE
    // ─────────────────────────────────────────────
    @Override
    public void removeAttendee(
            Integer meetingId,
            Integer attendeeId) {

        MeetingAttendee attendee =
                findById(attendeeId);

        if (!attendee.getMeeting()
                .getMeetingId()
                .equals(meetingId)) {
            throw new ResourceNotFoundException(
                    "Attendee", attendeeId);
        }

        validateMeetingActive(
                attendee.getMeeting());

        attendeeRepo.deleteById(attendeeId);

        activityLogService.log(
                "DELETE", "MeetingAttendee",
                attendeeId,
                "Remove: "
                        + attendee.getOfficer()
                        .getFullNameKh(),
                buildContext());
    }

    // ── Private Helpers ───────────────────────────

    private MeetingAttendee findById(
            Integer id) {
        return attendeeRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendee", id));
    }

    private Meeting findMeeting(Integer id) {
        return meetingRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ការប្រជុំ", id));
    }

    private Officer findOfficer(Integer id) {
        return officerRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រី", id));
    }

    // Fix validateMeetingActive — ប្រើ canEditAttendees
    private void validateMeetingActive(
            Meeting meeting) {

        String code =
                meeting.getStatusCode() != null
                        ? meeting.getStatusCode().getStatusCode()
                        : "";

        if (!MeetingStatusCode.canEditAttendees(code)) {
            throw new BusinessException(
                    "ការប្រជុំ \""
                            + meeting.getTitle()
                            + "\" ស្ថានភាព: " + code
                            + " — មិនអាចកែប្រែ Attendees");
        }
    }

    private ActivityLogContext buildContext() {
        try {
            var req =
                    ((ServletRequestAttributes)
                            RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();
            return securityUtils
                    .buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext.builder()
                    .build();
        }
    }
    // Fix — Check Officer ACTIVE before add
    private Officer findActiveOfficer(Integer id) {
        Officer officer =
                officerRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "មន្ត្រី", id));

        String status =
                officer.getStatusCode() != null
                        ? officer.getStatusCode().getStatusCode()
                        : "";

        if (!"ACTIVE".equals(status)) {
            throw new BusinessException(
                    "មន្ត្រី \""
                            + officer.getFullNameKh()
                            + "\" ស្ថានភាព: " + status
                            + " — មិនអាចបន្ថែម");
        }

        return officer;
    }
}