package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response
        .AttendeeResponse;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .AttendanceStatus;
import gov.kh.mcr.inspectorate.enums
        .MeetingStatusCode;
import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper.AttendeeMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security
        .SecurityUtils;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;
import org.springframework.web.context.request
        .RequestContextHolder;
import org.springframework.web.context.request
        .ServletRequestAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MeetingAttendeeServiceImpl
        implements MeetingAttendeeService {

    private final MeetingAttendeeRepository
            attendeeRepo;
    private final MeetingRepository
            meetingRepo;
    private final UserRepository
            userRepo;
    private final AttendeeMapper
            attendeeMapper;
    private final SecurityUtils
            securityUtils;
    private final NotificationService
            notificationService;
    private final ActivityLogService
            activityLogService;

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

        return attendeeMapper.toResponse(
                attendee);
    }


    @Override
    public AttendeeResponse addAttendee(
            Integer meetingId,
            AttendeeRequest request) {

        Meeting meeting = findMeeting(meetingId);

        validateMeetingActive(meeting);

        if (attendeeRepo
                .existsByMeeting_MeetingIdAndUser_UserId(
                        meetingId,
                        request.getUserId())) {
            throw new DuplicateResourceException(
                    "អ្នកប្រើប្រាស់ ឬមន្ត្រីរូបនេះ មានឈ្មោះក្នុងបញ្ជីសមាសភាពចូលរួមនៃកិច្ចប្រជុំនេះរួចរាល់ហើយ។");
        }

        User user = findActiveUser(
                request.getUserId());

        MeetingAttendee attendee =
                MeetingAttendee.builder()
                        .meeting(meeting)
                        .user(user)
                        .role(request.getRole())
                        .attendanceStatus(
                                AttendanceStatus.INVITED)
                        .build();

        MeetingAttendee saved =
                attendeeRepo.save(attendee);

        notificationService.createByUserId(
                user.getUserId(),
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
                        + user.getUserNameKh()
                        + " → " + meeting.getTitle(),
                buildContext());

        return attendeeMapper.toResponse(saved);
    }

    @Override
    public List<AttendeeResponse> addBulk(
            Integer meetingId,
            BulkAttendeeRequest request) {

        Meeting meeting = findMeeting(meetingId);
        validateMeetingActive(meeting);

        List<AttendeeResponse> results =
                new ArrayList<>();

        request.getUserIds().forEach(
                userId -> {

                    if (attendeeRepo
                            .existsByMeeting_MeetingIdAndUser_UserId(
                                    meetingId, userId)) {
                        log.warn(
                                "Duplicate user {}"
                                        + " in meeting {}",
                                userId, meetingId);
                        return;
                    }

                    userRepo.findById(userId)
                            .ifPresentOrElse(
                                    user -> {
                                        MeetingAttendee a =
                                                MeetingAttendee
                                                        .builder()
                                                        .meeting(meeting)
                                                        .user(user)
                                                        .role(
                                                                request
                                                                        .getRole())
                                                        .attendanceStatus(
                                                                AttendanceStatus
                                                                        .INVITED)
                                                        .build();

                                        results.add(
                                                attendeeMapper
                                                        .toResponse(
                                                                attendeeRepo
                                                                        .save(a)));

                                        notificationService
                                                .createByUserId(
                                                        userId,
                                                        "ការអញ្ជើញប្រជុំ",
                                                        "អញ្ជើញ: "
                                                                + meeting
                                                                .getTitle(),
                                                        NotificationType
                                                                .MEETING,
                                                        meetingId);
                                    },
                                    () -> log.warn(
                                            "User {} not found",
                                            userId));
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
            attendee.setNote(
                    request.getNote());
        }

        if (request.getAttendanceStatus()
                == AttendanceStatus.ATTENDED
                && attendee.getCheckInTime()
                == null) {
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

        return attendeeMapper.toResponse(
                saved);
    }

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
                        + attendee.getUser()
                        .getUserNameKh(),
                buildContext());
    }



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


    private User findActiveUser(Integer id) {

        User user = userRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User", id));

        String status =
                user.getStatusCode() != null
                        ? user.getStatusCode()
                        .getStatusCode()
                        : "";

        if (!"ACTIVE".equals(status)) {
            throw new BusinessException(
                    "មិនអាចជ្រើសរើស ឬបន្ថែមបានឡើយ ព្រោះគណនីរបស់មន្ត្រី «" + user.getUserNameKh() + "» "
                            + "មិនស្ថិតក្នុងស្ថានភាព «សកម្ម» ឡើយ (ស្ថានភាពបច្ចុប្បន្ន: " + status + ")");
        }

        return user;
    }

    private void validateMeetingActive(
            Meeting meeting) {

        String code =
                meeting.getStatusCode() != null
                        ? meeting.getStatusCode()
                        .getStatusCode()
                        : "";

        if (!MeetingStatusCode
                .canEditAttendees(code)) {
            throw new BusinessException(
                    "មិនអាចកែប្រែ ឬបន្ថែមសមាសភាពចូលរួមបានឡើយ ព្រោះកិច្ចប្រជុំ «" + meeting.getTitle() + "» "
                            + "ស្ថិតក្នុងស្ថានភាពដែលមិនអាចកែប្រែបានទៀតទេ (ស្ថានភាពបច្ចុប្បន្ន: " + code + ")");
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
}