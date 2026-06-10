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

    @Override
    @Transactional(readOnly = true)
    public MeetingResponse getById(Integer id) {
        return toResponseWithSummary(
                findById(id));
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
                    request,
                    null);
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

        Meeting saved = meetingRepo.save(meeting);

        activityLogService.log(
                "CREATE", "Meeting",
                saved.getMeetingId(),
                "បង្កើតកិច្ចប្រជុំថ្មី " + saved.getTitle(),
                buildContext());

        return toResponseWithSummary(saved);
    }

    @Override
    public MeetingResponse update(
            Integer id,
            MeetingRequest request) {

        Meeting meeting = findById(id);
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
    public MeetingResponse updateStatus(
            Integer id, String statusCode) {

        Meeting meeting = findById(id);

        String current =
                meeting.getStatusCode() != null
                        ? meeting.getStatusCode()
                          .getStatusCode()
                        : "";
        validateStatusTransition(
                current, statusCode);

        meeting.setStatusCode(
                findStatus(statusCode));

        activityLogService.log(
                "UPDATE", "Meeting",
                id,
                "ផ្លាស់ប្តូរស្ថានភាពកិច្ចប្រជុំ «" + meeting.getTitle() + "» ទៅជា៖ " + statusCode,
                buildContext());

        return toResponseWithSummary(
                meetingRepo.save(meeting));
    }

    @Override
    public void delete(Integer id) {

        Meeting meeting = findById(id);
        String code =
                meeting.getStatusCode() != null
                        ? meeting.getStatusCode()
                          .getStatusCode()
                        : "";

        if (!MeetingStatusCode.DRAFT
                .getCode().equals(code)) {
            throw new BusinessException(
                    "មិនអាចលុបកិច្ចប្រជុំបានឡើយ ដោយសារកិច្ចប្រជុំនេះស្ថិតក្នុងស្ថានភាព «"
                            + code
                            + "» (ប្រព័ន្ធអនុញ្ញាតឱ្យលុបតែគម្រោងព្រាង ប៉ុណ្ណោះ)។");
        }

        meetingRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "Meeting",
                id, "លុបទិន្នន័យកិច្ចប្រជុំ " + meeting.getTitle(),
                buildContext());
    }

    private void validateTime(
            MeetingRequest request) {

        if (request.getEndTime()
                .isBefore(request.getStartTime())
                || request.getEndTime()
                .equals(request.getStartTime())) {
            throw new BusinessException(
                    "មកាលបរិច្ឆេទ ឬម៉ោងបញ្ចប់ត្រូវតែនៅក្រោយកាលបរិច្ឆេទ ឬម៉ោងចាប់ផ្ដើម។");
        }
    }

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
                    "មិនអាចកក់បានឡើយ ដោយសារបន្ទប់ប្រជុំ «"
                            + c.getRoom().getRoomCode()
                            + "» ត្រូវបានកក់រួចហើយ ចាប់ពីម៉ោង "
                            + c.getStartTime()
                            + " ដល់ "
                            + c.getEndTime()
                            + " សម្រាប់កិច្ចប្រជុំ៖ «"
                            + c.getTitle() + "»។");
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

    private void validateStatusTransition(
            String current, String next) {

        if (MeetingStatusCode.isFinal(current)) {
            throw new BusinessException(
                    "មិនអាចផ្លាស់ប្តូរបានឡើយ ដោយសារកិច្ចប្រជុំនេះស្ថិតក្នុងស្ថានភាពចុងក្រោយ «"
                            + current
                            + "» រួចរាល់ហើយ។");
        }

        if (MeetingStatusCode.IN_PROGRESS
                .getCode().equals(current)
                && MeetingStatusCode.CANCELLED
                .getCode().equals(next)) {
            throw new BusinessException(
                    "មិនអាចលុបចោលកិច្ចប្រជុំបានឡើយ ដោយសារកិច្ចប្រជុំកំពុងតែប្រព្រឹត្តទៅ");
        }
    }

    private Meeting findById(Integer id) {
        return meetingRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យកិច្ចប្រជុំដែលមានលេខសម្គាល់ ", id));
    }

    private MeetingRoom findRoom(Integer id) {
        return roomRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យបន្ទប់ប្រជុំដែលមានលេខសម្គាល់ ", id));
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
}