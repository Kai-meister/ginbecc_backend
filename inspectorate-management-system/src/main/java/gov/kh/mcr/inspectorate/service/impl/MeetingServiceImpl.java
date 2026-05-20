package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.AttendanceRequest;
import gov.kh.mcr.inspectorate.dto.request.AttendeeRequest;
import gov.kh.mcr.inspectorate.dto.request.MeetingRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.dto.response.RoomAvailabilityResponse;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
import gov.kh.mcr.inspectorate.enums.MeetingStatusCode;
import gov.kh.mcr.inspectorate.enums.MeetingType;
import gov.kh.mcr.inspectorate.enums.RoomStatus;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.exception.RoomConflictException;
import gov.kh.mcr.inspectorate.mapper.MeetingMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.MeetingService;
import gov.kh.mcr.inspectorate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingRoomRepository meetingRoomRepository;
    private final MeetingAttendeeRepository attendeeRepository;
    private final OfficerRepository officerRepository;
    private final LookupMeetingStatusRepository lookupStatusRepository;
    private final MeetingMapper meetingMapper;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MeetingResponse> getAll(
            int page, int size, String status, Integer roomId) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("meetingDate").descending());

        Page<Meeting> result;
        if (status != null && roomId != null) {
            result = meetingRepository
                    .findByRoom_RoomIdAndStatusCode_StatusCode(
                            roomId, status, pageable);
        } else if (status != null) {
            result = meetingRepository
                    .findByStatusCode_StatusCode(status, pageable);
        } else {
            result = meetingRepository.findAll(pageable);
        }

        return PageResponse.of(result.map(meetingMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public MeetingResponse getById(Integer id) {
        return meetingMapper.toResponse(findMeetingById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetingResponse> getCalendar(int month, int year) {
        return meetingRepository.findByMonthAndYear(month, year)
                .stream()
                .map(meetingMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomAvailabilityResponse checkAvailability(
            Integer roomId, LocalDate date,
            LocalTime startTime, LocalTime endTime) {

        MeetingRoom room = findRoomById(roomId);
        List<Meeting> allMeetings =
                meetingRepository.findByRoomAndDate(roomId, date);

        List<Meeting> conflicts = allMeetings.stream()
                .filter(m -> isOverlap(startTime, endTime,
                        m.getStartTime(), m.getEndTime()))
                .toList();

        var conflictInfos = conflicts.stream()
                .map(m -> RoomAvailabilityResponse.ConflictInfo.builder()
                        .meetingId(m.getMeetingId())
                        .title(m.getTitle())
                        .startTime(m.getStartTime())
                        .endTime(m.getEndTime())
                        .organizerName(m.getOrganizer() != null
                                ? m.getOrganizer().getUserNameKh() : "")
                        .statusCode(m.getStatusCode() != null
                                ? m.getStatusCode().getStatusCode() : "")
                        .build())
                .toList();

        return RoomAvailabilityResponse.builder()
                .roomId(room.getRoomId())
                .roomCode(room.getRoomCode())
                .roomLocation(room.getLocation())
                .meetingDate(date)
                .requestedStart(startTime)
                .requestedEnd(endTime)
                .conflicts(conflictInfos)
                .availableSlots(computeSlots(allMeetings))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetingResponse> getRoomSchedule(
            Integer roomId, LocalDate date) {
        return meetingRepository.findByRoomAndDate(roomId, date)
                .stream()
                .map(meetingMapper::toResponse)
                .toList();
    }

    @Override
    public MeetingResponse create(MeetingRequest request) {
        validateMeetingRequest(request, null);

        Meeting meeting = meetingMapper.toEntity(request);

        if (request.getRoomId() != null) {
            meeting.setRoom(findRoomById(request.getRoomId()));
        }
        meeting.setStatusCode(
                findMeetingStatus(request.getStatusCode()));
        securityUtils.getCurrentUser()
                .ifPresent(meeting::setOrganizer);

        Meeting saved = meetingRepository.save(meeting);
        activityLogService.log("CREATE", "Meeting",
                saved.getMeetingId(),
                "បង្កើតប្រជុំ: " + saved.getTitle());

        return meetingMapper.toResponse(saved);
    }

    @Override
    public MeetingResponse update(Integer id, MeetingRequest request) {
        Meeting meeting = findMeetingById(id);
        validateMeetingRequest(request, id);

        meeting.setTitle(request.getTitle());
        meeting.setMeetingDate(request.getMeetingDate());
        meeting.setStartTime(request.getStartTime());
        meeting.setEndTime(request.getEndTime());
        meeting.setMeetingType(request.getMeetingType());
        meeting.setMeetingLink(request.getMeetingLink());
        meeting.setAgenda(request.getAgenda());
        meeting.setNote(request.getNote());

        if (request.getRoomId() != null) {
            meeting.setRoom(findRoomById(request.getRoomId()));
        }
        meeting.setStatusCode(
                findMeetingStatus(request.getStatusCode()));

        activityLogService.log("UPDATE", "Meeting", id,
                "កែប្រែ: " + meeting.getTitle());

        return meetingMapper.toResponse(meetingRepository.save(meeting));
    }

    @Override
    public void addAttendeesBulk(Integer meetingId,
                                 List<AttendeeRequest> requests) {
        Meeting meeting = findMeetingById(meetingId);

        requests.forEach(req -> {
            Officer officer = officerRepository
                    .findById(req.getOfficerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "មន្ត្រី", req.getOfficerId()));

            boolean exists = attendeeRepository
                    .findByMeeting_MeetingIdAndOfficer_OfficerId(
                            meetingId, req.getOfficerId())
                    .isPresent();

            if (!exists) {
                attendeeRepository.save(
                        MeetingAttendee.builder()
                                .meeting(meeting)
                                .officer(officer)
                                .role(req.getRole())
                                .build());

                notificationService.createByOfficerId(
                        officer.getOfficerId(),
                        "ការអញ្ជើញប្រជុំ",
                        "អញ្ជើញ: " + meeting.getTitle()
                                + " (" + meeting.getMeetingDate() + ")",
                        "MEETING",
                        meeting.getMeetingId(),
                        "MEETING");
            }
        });
    }

    @Override
    public void updateAttendance(Integer meetingId, Integer attendeeId,
                                 AttendanceRequest request) {
        MeetingAttendee attendee = attendeeRepository
                .findById(attendeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "អ្នកចូលរួម", attendeeId));

        if (!attendee.getMeeting().getMeetingId().equals(meetingId)) {
            throw new ResourceNotFoundException(
                    "អ្នកចូលរួម", attendeeId);
        }

        attendee.setAttendanceStatus(request.getAttendanceStatus());
        attendee.setNote(request.getNote());

        if (request.getAttendanceStatus()
                == AttendanceStatus.PRESENT) {
            attendee.setCheckInTime(java.time.LocalDateTime.now());
        }

        attendeeRepository.save(attendee);
    }

    @Override
    public void delete(Integer id) {
        findMeetingById(id);
        meetingRepository.deleteById(id);
        activityLogService.log("DELETE", "Meeting", id, "លុបប្រជុំ");
    }

    // Private Helpers
    private void validateMeetingRequest(MeetingRequest req,
                                        Integer excludeId) {
        if (!req.getStartTime().isBefore(req.getEndTime())) {
            throw new BusinessException(
                    "ម៉ោងចាប់ផ្តើម ត្រូវតែមុន ម៉ោងបញ្ចប់");
        }

        if (req.getMeetingType() == MeetingType.ONLINE) {
            if (!StringUtils.hasText(req.getMeetingLink())) {
                throw new BusinessException(
                        "ប្រជុំ Online ត្រូវការ Meeting Link");
            }
            return; // No room conflict for ONLINE
        }

        if (req.getRoomId() == null) return;

        MeetingRoom room = findRoomById(req.getRoomId());

        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new BusinessException(
                    "បន្ទប់ [" + room.getRoomCode()
                            + "] កំពុងជួសជុល");
        }
        List<String> ignored = List.of(
                MeetingStatusCode.CANCELLED.getCode(),
                MeetingStatusCode.COMPLETED.getCode());

        List<Meeting> conflicts = meetingRepository.findConflicts(
                        req.getRoomId(),
                        req.getMeetingDate(),
                        req.getStartTime(),
                        req.getEndTime(),
                        ignored);

        if (!conflicts.isEmpty()) {
            Meeting c = conflicts.get(0);
            throw new RoomConflictException(
                    room.getRoomCode(),
                    c.getTitle(),
                    c.getStartTime() + " - " + c.getEndTime());
        }
    }

    private boolean isOverlap(LocalTime s1, LocalTime e1,
                              LocalTime s2, LocalTime e2) {
        return s1.isBefore(e2) && e1.isAfter(s2);
    }

    private List<RoomAvailabilityResponse.AvailableSlot>
    computeSlots(List<Meeting> meetings) {

        LocalTime workStart = LocalTime.of(7, 0);
        LocalTime workEnd   = LocalTime.of(17, 0);
        LocalTime cursor    = workStart;

        List<RoomAvailabilityResponse.AvailableSlot> slots =
                new ArrayList<>();

        List<Meeting> sorted = meetings.stream()
                .sorted(Comparator.comparing(Meeting::getStartTime))
                .collect(Collectors.toList());

        for (Meeting m : sorted) {
            if (cursor.isBefore(m.getStartTime())) {
                long mins = ChronoUnit.MINUTES
                        .between(cursor, m.getStartTime());
                if (mins >= 30) {
                    slots.add(
                            RoomAvailabilityResponse.AvailableSlot
                                    .builder()
                                    .from(cursor)
                                    .to(m.getStartTime())
                                    .durationMinutes(mins)
                                    .build());
                }
            }
            if (cursor.isBefore(m.getEndTime())) {
                cursor = m.getEndTime();
            }
        }

        if (cursor.isBefore(workEnd)) {
            long mins = ChronoUnit.MINUTES.between(cursor, workEnd);
            if (mins >= 30) {
                slots.add(
                        RoomAvailabilityResponse.AvailableSlot
                                .builder()
                                .from(cursor)
                                .to(workEnd)
                                .durationMinutes(mins)
                                .build());
            }
        }
        return slots;
    }

    private Meeting findMeetingById(Integer id) {
        return meetingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ការប្រជុំ", id));
    }

    private MeetingRoom findRoomById(Integer id) {
        return meetingRoomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("បន្ទប់ប្រជុំ", id));
    }

    private LookupMeetingStatus findMeetingStatus(String code) {
        return lookupStatusRepository.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ស្ថានភាព", code));
    }
}