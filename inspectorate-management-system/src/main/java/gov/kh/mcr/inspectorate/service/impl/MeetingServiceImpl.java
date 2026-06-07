package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
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

    // ─────────────────────────────────────────────
    // GET ALL
    // ─────────────────────────────────────────────
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

        Page<Meeting> result;

        if (status != null && roomId != null) {
            result = meetingRepo
                    .findByStatusCode_StatusCode(
                            status, pageable);
        } else if (status != null) {
            result = meetingRepo
                    .findByStatusCode_StatusCode(
                            status, pageable);
        } else if (roomId != null) {
            result = meetingRepo
                    .findByRoom_RoomId(
                            roomId, pageable);
        } else {
            result = meetingRepo.findAll(pageable);
        }

        return PageResponse.of(
                result.map(this::toResponseWithSummary));
    }

    // ─────────────────────────────────────────────
    // GET BY ID
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public MeetingResponse getById(Integer id) {
        return toResponseWithSummary(
                findById(id));
    }

    // ─────────────────────────────────────────────
    // GET CALENDAR
    // ─────────────────────────────────────────────
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

    // ─────────────────────────────────────────────
    // GET ROOM SCHEDULE
    // ─────────────────────────────────────────────
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

    // ─────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────
    @Override
    public MeetingResponse create(
            MeetingRequest request) {

        // ១. Validate time
        validateTime(request);

        // ២. Conflict check (room only)
        if (request.getRoomId() != null) {
            checkConflict(
                    request.getRoomId(),
                    request,
                    null);
        }

        Meeting meeting =
                meetingMapper.toEntity(request);

        // ៣. Set room (optional)
        if (request.getRoomId() != null) {
            meeting.setRoom(
                    findRoom(request.getRoomId()));
        }

        // ៤. Set status
        meeting.setStatusCode(
                findStatus(request.getStatusCode()));

        // ៥. Set organizer
        securityUtils.getCurrentUser()
                .ifPresent(meeting::setOrganizer);

        Meeting saved = meetingRepo.save(meeting);

        activityLogService.log(
                "CREATE", "Meeting",
                saved.getMeetingId(),
                "បង្កើត: " + saved.getTitle(),
                buildContext());

        return toResponseWithSummary(saved);
    }

    // ─────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────
    @Override
    public MeetingResponse update(
            Integer id,
            MeetingRequest request) {

        Meeting meeting = findById(id);

        // Fix — Block update if final status
        validateCanUpdate(meeting);

        // Validate time
        validateTime(request);

        // Conflict check (exclude self)
        if (request.getRoomId() != null) {
            checkConflict(
                    request.getRoomId(),
                    request,
                    id);
        }

        meetingMapper.updateEntity(
                request, meeting);

        // Update room
        if (request.getRoomId() != null) {
            meeting.setRoom(
                    findRoom(request.getRoomId()));
        } else {
            meeting.setRoom(null);
        }

        // Update status
        meeting.setStatusCode(
                findStatus(request.getStatusCode()));

        activityLogService.log(
                "UPDATE", "Meeting",
                id, "កែប្រែ: " + meeting.getTitle(),
                buildContext());

        return toResponseWithSummary(
                meetingRepo.save(meeting));
    }

    // ─────────────────────────────────────────────
    // UPDATE STATUS
    // ─────────────────────────────────────────────
    @Override
    public MeetingResponse updateStatus(
            Integer id, String statusCode) {

        Meeting meeting = findById(id);

        String current =
                meeting.getStatusCode() != null
                        ? meeting.getStatusCode()
                          .getStatusCode()
                        : "";

        // Fix — Validate status transition
        validateStatusTransition(
                current, statusCode);

        meeting.setStatusCode(
                findStatus(statusCode));

        activityLogService.log(
                "UPDATE", "Meeting",
                id,
                "ស្ថានភាព → " + statusCode,
                buildContext());

        return toResponseWithSummary(
                meetingRepo.save(meeting));
    }

    // ─────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────
    @Override
    public void delete(Integer id) {

        Meeting meeting = findById(id);

        // Fix — Only DRAFT can be deleted
        String code =
                meeting.getStatusCode() != null
                        ? meeting.getStatusCode()
                          .getStatusCode()
                        : "";

        if (!MeetingStatusCode.DRAFT
                .getCode().equals(code)) {
            throw new BusinessException(
                    "មិនអាចលុបការប្រជុំ"
                            + " ស្ថានភាព: " + code
                            + " — DRAFT ប៉ុណ្ណោះ");
        }

        meetingRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "Meeting",
                id, "លុប: " + meeting.getTitle(),
                buildContext());
    }

    // ── Private Validations ───────────────────────

    // Fix — Time validation
    private void validateTime(
            MeetingRequest request) {

        if (request.getEndTime()
                .isBefore(request.getStartTime())
                || request.getEndTime()
                .equals(request.getStartTime())) {
            throw new BusinessException(
                    "ម៉ោងបញ្ចប់"
                            + " ត្រូវធំជាង ម៉ោងចាប់ផ្ដើម");
        }
    }

    // Fix — Conflict check
    private void checkConflict(
            Integer roomId,
            MeetingRequest request,
            Integer excludeId) {

        List<String> ignored = ignoredStatuses();

        List<Meeting> conflicts =
                excludeId != null
                        ? meetingRepo.findConflictsExclude(
                        roomId,
                        request.getMeetingDate(),
                        request.getStartTime(),
                        request.getEndTime(),
                        excludeId, ignored)
                        : meetingRepo.findConflicts(
                        roomId,
                        request.getMeetingDate(),
                        request.getStartTime(),
                        request.getEndTime(),
                        ignored);

        if (!conflicts.isEmpty()) {
            Meeting c = conflicts.get(0);
            throw new BusinessException(
                    "បន្ទប់ \""
                            + c.getRoom().getRoomCode()
                            + "\" ត្រូវបានកក់"
                            + " ពេល: "
                            + c.getStartTime()
                            + " - " + c.getEndTime()
                            + " ដោយ: " + c.getTitle());
        }
    }

    // Fix — Block update if COMPLETED/CANCELLED
    private void validateCanUpdate(
            Meeting meeting) {

        String code =
                meeting.getStatusCode() != null
                        ? meeting.getStatusCode()
                          .getStatusCode()
                        : "";

        if (!MeetingStatusCode.canUpdate(code)) {
            throw new BusinessException(
                    "មិនអាចកែប្រែ"
                            + " ស្ថានភាព: " + code);
        }
    }

    // Fix — Status transition rules
    private void validateStatusTransition(
            String current, String next) {

        // Cannot change from final state
        if (MeetingStatusCode.isFinal(current)) {
            throw new BusinessException(
                    "ស្ថានភាព " + current
                            + " មិនអាចផ្លាស់ប្ដូរ");
        }

        // Cannot cancel if IN_PROGRESS
        if (MeetingStatusCode.IN_PROGRESS
                .getCode().equals(current)
                && MeetingStatusCode.CANCELLED
                .getCode().equals(next)) {
            throw new BusinessException(
                    "ការប្រជុំ IN_PROGRESS"
                            + " មិនអាច CANCEL");
        }
    }

    // ── Private Helpers ───────────────────────────

    private Meeting findById(Integer id) {
        return meetingRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ការប្រជុំ", id));
    }

    private MeetingRoom findRoom(Integer id) {
        return roomRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "បន្ទប់ប្រជុំ", id));
    }

    private LookupMeetingStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ស្ថានភាពប្រជុំ", code));
    }

    private List<String> ignoredStatuses() {
        return List.of(
                MeetingStatusCode.CANCELLED.getCode(),
                MeetingStatusCode.COMPLETED.getCode());
    }

    // Build response with attendee summary
    private MeetingResponse toResponseWithSummary(
            Meeting meeting) {

        MeetingResponse dto =
                meetingMapper.toResponse(meeting);

        Integer meetingId = meeting.getMeetingId();

        // ── Attendees list ────────────────────────────
        // Fix — Load + Map attendees
        List<AttendeeResponse> attendees =
                attendeeRepo
                        .findByMeeting_MeetingId(meetingId)
                        .stream()
                        .map(attendeeMapper::toResponse)
                        .toList();

        dto.setAttendees(attendees);

        // ── Summary counts ────────────────────────────
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
}