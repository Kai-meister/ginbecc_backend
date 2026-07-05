package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
import gov.kh.mcr.inspectorate.enums.MeetingRoomStatus;
import gov.kh.mcr.inspectorate.enums.MeetingStatusCode;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper.AttendeeMapper;
import gov.kh.mcr.inspectorate.mapper.MeetingMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MeetingServiceImpl
        implements MeetingService {

    private final MeetingRepository              meetingRepo;
    private final MeetingRoomRepository          roomRepo;
    private final LookupMeetingStatusRepository  statusRepo;
    private final MeetingAttendeeRepository      attendeeRepo;
    private final MeetingMapper                  meetingMapper;
    private final SecurityUtils                  securityUtils;
    private final ActivityLogService             activityLogService;
    private final AttendeeMapper attendeeMapper;
    @Override
    @Transactional(readOnly = true)
    public PageResponse<MeetingResponse> getAll(
            int page, int size,
            String status, Integer roomId) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("meetingDate").descending()
                        .and(Sort.by("startTime")
                                .descending()));

        Integer deptScope =
                securityUtils
                        .canBypassDepartmentScope()
                        ? null
                        : securityUtils
                        .getCurrentDepartmentId();

        Page<Meeting> result;

        if (deptScope != null) {

            result = meetingRepo
                    .findByOrganizer_Officer_Department_DepartmentId(
                            deptScope, pageable);
        } else if (status != null) {
            result = meetingRepo
                    .findByStatusCode_StatusCode(
                            status, pageable);
        } else if (roomId != null) {
            result = meetingRepo
                    .findByRoom_RoomId(
                            roomId, pageable);
        } else {
            result = meetingRepo
                    .findAll(pageable);
        }

        return PageResponse.of(
                result.map(
                        this::toResponseWithSummary));
    }
    @Override
    @Transactional(readOnly = true)
    public MeetingResponse getById(Integer id) {

        Meeting meeting = findById(id);

        Integer organizerDeptId =
                meeting.getOrganizer() != null
                        && meeting.getOrganizer()
                        .getOfficer() != null
                        && meeting.getOrganizer()
                        .getOfficer()
                        .getDepartment() != null
                        ? meeting.getOrganizer()
                        .getOfficer()
                        .getDepartment()
                        .getDepartmentId()
                        : null;

        securityUtils.validateDepartmentScope(
                organizerDeptId);

        return toResponseWithSummary(meeting);
    }
    @Override
    @Transactional(readOnly = true)
    public List<MeetingResponse> getCalendar(
            int month, int year) {

        return meetingRepo
                .findByMonthAndYear(month, year)
                .stream()
                .map(this::toResponseWithSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetingResponse> getRoomSchedule(
            Integer roomId, LocalDate date) {

        List<String> ignored = ignoredStatuses();

        return meetingRepo
                .findRoomSchedule(
                        roomId, date, ignored)
                .stream()
                .map(this::toResponseWithSummary)
                .toList();
    }


    @Override
    public MeetingResponse create(
            MeetingRequest request) {

        validateTime(request);

        if (request.getRoomId() != null) {
            checkConflict(
                    request.getRoomId(),
                    request, null);
        }

        Meeting meeting =
                meetingMapper.toEntity(request);

        if (request.getRoomId() != null) {
            meeting.setRoom(
                    findRoom(request.getRoomId()));
        }

        meeting.setStatusCode(
                findStatus(request.getStatusCode()));

        securityUtils.getCurrentUser()
                .ifPresent(meeting::setOrganizer);

        Meeting saved =
                meetingRepo.save(meeting);

        activityLogService.log(
                "CREATE", "Meeting",
                saved.getMeetingId(),
                "បង្កើត: " + saved.getTitle(),
                buildContext());

        return toResponseWithSummary(saved);
    }
    @Override
    public MeetingResponse update(
            Integer id,
            MeetingRequest request) {

        Meeting meeting = findById(id);
        Integer organizerDeptId =
                meeting.getOrganizer() != null
                        && meeting.getOrganizer()
                        .getOfficer() != null
                        && meeting.getOrganizer()
                        .getOfficer()
                        .getDepartment() != null
                        ? meeting.getOrganizer()
                        .getOfficer()
                        .getDepartment()
                        .getDepartmentId()
                        : null;

        securityUtils.validateDepartmentScope(
                organizerDeptId);

        validateCanUpdate(meeting);
        validateTime(request);

        validateCanUpdate(meeting);

        validateTime(request);
        if (request.getRoomId() != null) {
            checkConflict(
                    request.getRoomId(),
                    request,
                    id);
        }

        meetingMapper.updateEntity(
                request, meeting);
        if (request.getRoomId() != null) {
            meeting.setRoom(
                    findRoom(request.getRoomId()));
        } else {
            meeting.setRoom(null);
        }
        meeting.setStatusCode(
                findStatus(request.getStatusCode()));

        activityLogService.log(
                "UPDATE", "Meeting",
                id, "កែប្រែព័ត៌មានកិច្ចប្រជុំ " + meeting.getTitle(),
                buildContext());

        return toResponseWithSummary(
                meetingRepo.save(meeting));
    }


    @Override
    public void delete(Integer id) {

        Meeting meeting = findById(id);

        Integer organizerDeptId =
                meeting.getOrganizer() != null
                        && meeting.getOrganizer()
                        .getOfficer() != null
                        && meeting.getOrganizer()
                        .getOfficer()
                        .getDepartment() != null
                        ? meeting.getOrganizer()
                        .getOfficer()
                        .getDepartment()
                        .getDepartmentId()
                        : null;

        securityUtils.validateDepartmentScope(
                organizerDeptId);

        String code =
                meeting.getStatusCode() != null
                        ? meeting.getStatusCode()
                        .getStatusCode()
                        : "";

        if (!MeetingStatusCode.DRAFT
                .getCode().equals(code)) {
            throw new BusinessException(
                    "មិនអាចលុបកិច្ចប្រជុំនេះបានឡើយ ព្រោះកិច្ចប្រជុំដែលអាចលុបបាន លុះត្រាតែស្ថិតក្នុងស្ថានភាព «ឯកសារព្រាង/រក្សាទុកបណ្តោះអាសន្ន» ប៉ុណ្ណោះ "
                            + "(ស្ថានភាពបច្ចុប្បន្ន: " + code + ")");
        }

        meetingRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "Meeting", id,
                "លុប: " + meeting.getTitle(),
                buildContext());
    }

    private void validateTime(
            MeetingRequest request) {

        if (request.getEndTime()
                .isBefore(request.getStartTime())
                || request.getEndTime()
                .equals(request.getStartTime())) {
            throw new BusinessException(
                    "កាលបរិច្ឆេទ ឬម៉ោងបញ្ចប់ត្រូវតែនៅក្រោយកាលបរិច្ឆេទ ឬម៉ោងចាប់ផ្ដើម។");
        }
    }


    private void validateCanUpdate(
            Meeting meeting) {

        String code =
                meeting.getStatusCode() != null
                        ? meeting.getStatusCode()
                          .getStatusCode()
                        : "";

        if (!MeetingStatusCode.canUpdate(code)) {
            throw new BusinessException(
                    "មិនអាចកែប្រែព័ត៌មានបានឡើយ ដោយសារកិច្ចប្រជុំនេះស្ថិតក្នុងស្ថានភាព «"
                            + code
                            + "» ដែលត្រូវបានចាក់សោរួចហើយ។");
        }
    }

    private Meeting findById(Integer id) {
        return meetingRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យកិច្ចប្រជុំដែលមានលេខសម្គាល់ ", id));
    }

    private MeetingRoom findRoom(Integer id) {
        MeetingRoom room = roomRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យបន្ទប់ប្រជុំដែលមានលេខសម្គាល់ ", id));

        return room;
    }

    private LookupMeetingStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យស្ថានភាពកិច្ចប្រជុំដែលមានកូដ ", code));
    }

    private List<String> ignoredStatuses() {
        return List.of(
                MeetingStatusCode.CANCELLED.getCode(),
                MeetingStatusCode.COMPLETED.getCode());
    }

    private MeetingResponse toResponseWithSummary(
            Meeting meeting) {

        MeetingResponse dto =
                meetingMapper.toResponse(meeting);

        Integer meetingId = meeting.getMeetingId();
        List<AttendeeResponse> attendees =
                attendeeRepo
                        .findByMeeting_MeetingId(meetingId)
                        .stream()
                        .map(attendeeMapper::toResponse)
                        .toList();

        dto.setAttendees(attendees);

        dto.setTotalAttendees(attendees.size());

        dto.setAttendedCount(
                (int) attendees.stream()
                        .filter(a ->
                                AttendanceStatus.ATTENDED.name()
                                        .equals(a.getAttendanceStatus()
                                                .name()))
                        .count());

        dto.setAbsentCount(
                (int) attendees.stream()
                        .filter(a ->
                                AttendanceStatus.ABSENT.name()
                                        .equals(a.getAttendanceStatus()
                                                .name()))
                        .count());

        dto.setInvitedCount(
                (int) attendees.stream()
                        .filter(a ->
                                AttendanceStatus.INVITED.name()
                                        .equals(a.getAttendanceStatus()
                                                .name()))
                        .count());

        return dto;
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

    private void syncRoomStatusOnDecision(
            Meeting meeting,
            String newStatusCode) {

        MeetingRoom room = meeting.getRoom();
        if (room == null) return;

        // Fix — ប្រសិនបើ Cancel ឬ Complete
        // → Room ត្រូវ AVAILABLE ភ្លាម
        if ("CANCELLED".equals(newStatusCode)
                || "COMPLETED".equals(
                newStatusCode)) {

            // ប្រាកដ Room ត្រូវបន្ទប់
            // Meeting ដែល Cancel/Complete
            if (room.getStatus()
                    == MeetingRoomStatus.IN_USE
                    && room.getCurrentMeeting()
                    != null
                    && room.getCurrentMeeting()
                    .getMeetingId()
                    .equals(meeting
                            .getMeetingId())) {

                room.setStatus(
                        MeetingRoomStatus.AVAILABLE);
                room.setCurrentMeeting(null);
                roomRepo.save(room);

                log.info(
                        "Real-time Room update:"
                                + " [{}] → AVAILABLE"
                                + " (Meeting [{}] {})",
                        room.getRoomCode(),
                        meeting.getTitle(),
                        newStatusCode);
            }
        }
    }
    private void checkConflict(
            Integer roomId,
            MeetingRequest request,
            Integer excludeMeetingId) {

        List<Meeting> conflicts =
                meetingRepo.findConflicts(
                        roomId,
                        request.getMeetingDate(),
                        request.getStartTime(),
                        request.getEndTime(),
                        excludeMeetingId);

        if (!conflicts.isEmpty()) {
            Meeting c = conflicts.get(0);
            throw new BusinessException(
                    "បន្ទប់ប្រជុំ"
                            + " ត្រូវបានកក់ហើយ"
                            + " — ទំនាស់ជាមួយ:"
                            + " \""
                            + c.getTitle()
                            + "\""
                            + " ("
                            + c.getStartTime()
                            + " - "
                            + c.getEndTime()
                            + ")"
                            + " ថ្ងៃ "
                            + c.getMeetingDate()
                            + " — សូមជ្រើស"
                            + " ម៉ោង ឬ បន្ទប់"
                            + " ផ្សេង");
        }
    }

    @Override
    public MeetingResponse updateStatus(
            Integer id,
            String newStatusCode) {

        Meeting meeting = findById(id);

        validateStatusTransition(
                meeting.getStatusCode()
                        .getStatusCode(),
                newStatusCode);

        LookupMeetingStatus newStatus =
                statusRepo.findById(newStatusCode)
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "Meeting Status",
                                        newStatusCode));

        meeting.setStatusCode(newStatus);
        Meeting saved =
                meetingRepo.save(meeting);

        // Fix — Real-time Room update
        syncRoomStatusOnDecision(
                meeting, newStatusCode);

        activityLogService.log(
                "UPDATE", "Meeting", id,
                "Status: " + newStatusCode,
                buildContext());

        return toResponseWithSummary(saved);
    }

    private void validateStatusTransition(
            String current,
            String target) {

        // Fix — Cannot go back from final states
        if ("COMPLETED".equals(current)
                || "CANCELLED".equals(current)) {
            throw new BusinessException(
                    "ការប្រជុំ Status: "
                            + current
                            + " — មិនអាចប្ដូរ Status"
                            + " ទៀតបានទេ (Final State)");
        }

        // Fix — Valid transitions
        boolean valid = switch (current) {
            case "DRAFT" -> List.of(
                            "SCHEDULED", "CANCELLED")
                    .contains(target);
            case "SCHEDULED" -> List.of(
                            "CONFIRMED", "POSTPONED",
                            "CANCELLED", "IN_PROGRESS")
                    .contains(target);
            case "CONFIRMED" -> List.of(
                            "POSTPONED", "CANCELLED",
                            "IN_PROGRESS")
                    .contains(target);
            case "POSTPONED" -> List.of(
                            "SCHEDULED", "CANCELLED")
                    .contains(target);
            case "RESCHEDULED" -> List.of(
                            "CONFIRMED", "CANCELLED")
                    .contains(target);
            case "IN_PROGRESS" -> List.of(
                            "COMPLETED", "CANCELLED")
                    .contains(target);
            default -> false;
        };

        if (!valid) {
            throw new BusinessException(
                    "មិនអាចប្ដូរ Status"
                            + " ពី " + current
                            + " ទៅ " + target
                            + " — ការប្ដូរ Status"
                            + " មិនត្រឹមត្រូវ");
        }
    }


}