package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request.MeetingRoomRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingRoomResponse;
import gov.kh.mcr.inspectorate.dto.response.RoomScheduleResponse;
import gov.kh.mcr.inspectorate.dto.response.RoomScheduleResponse.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.MeetingRoomStatus;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper.MeetingRoomMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import gov.kh.mcr.inspectorate.service.MeetingRoomService;
import gov.kh.mcr.inspectorate.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MeetingRoomServiceImpl implements MeetingRoomService {

    private final MeetingRoomRepository roomRepo;
    private final MeetingRepository meetingRepo;
    private final AttachmentRepository attachmentRepo;
    private final AttachmentService attachmentService;
    private final MinioService minioService;
    private final MeetingRoomMapper roomMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;
    private static final List<String> ALLOWED_IMAGE_TYPES =
            List.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    @Transactional(readOnly = true)
    public List<MeetingRoomResponse> getAll(MeetingRoomStatus status) {

        List<MeetingRoom> rooms = status != null
                ? roomRepo.findByStatus(status)
                : roomRepo.findAllByOrderByRoomCodeAsc();

        return rooms.stream()
                .map(this::toResponseWithImage)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MeetingRoomResponse getById(Integer id) {
        return toResponseWithImage(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public RoomScheduleResponse getSchedule(
            Integer roomId, LocalDate from, LocalDate to) {

        MeetingRoom room = findById(roomId);

        LocalDate effectiveFrom = from != null ? from : LocalDate.now();
        LocalDate effectiveTo = to != null ? to : effectiveFrom.plusDays(30);

        List<Meeting> meetings = meetingRepo.findByRoomSchedule(
                roomId, effectiveFrom, effectiveTo);

        LocalDate today = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        List<RoomBookingSlot> bookings = meetings.stream()
                .map(m -> {
                    boolean isNow =
                            m.getMeetingDate().equals(today)
                                    && m.getStartTime() != null
                                    && m.getEndTime() != null
                                    && !nowTime.isBefore(m.getStartTime())
                                    && nowTime.isBefore(m.getEndTime());

                    boolean isPast =
                            m.getMeetingDate().isBefore(today)
                                    || (m.getMeetingDate().equals(today)
                                    && m.getEndTime() != null
                                    && nowTime.isAfter(m.getEndTime()));

                    boolean isCancelled =
                            "CANCELLED".equals(m.getStatusCode().getStatusCode());

                    return RoomBookingSlot.builder()
                            .meetingId(m.getMeetingId())
                            .meetingTitle(m.getTitle())
                            .organizerName(
                                    m.getOrganizer() != null
                                            ? m.getOrganizer().getUserNameKh() : "")
                            .date(m.getMeetingDate())
                            .startTime(m.getStartTime())
                            .endTime(m.getEndTime())
                            .statusCode(m.getStatusCode().getStatusCode())
                            .statusLabel(m.getStatusCode().getLabelKh())
                            .isNow(isNow)
                            .isPast(isPast)
                            .isCancelled(isCancelled)
                            .build();
                })
                .toList();

        List<RoomTimeSlot> todaySlots = buildTimeSlots(roomId, today, nowTime);

        return RoomScheduleResponse.builder()
                .roomId(roomId)
                .roomCode(room.getRoomCode())
                .status(room.getStatus())
                .statusLabel(room.getStatus().getLabelKh())
                .bookings(bookings)
                .todaySlots(todaySlots)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomScheduleResponse getAvailability(Integer roomId, LocalDate date) {

        MeetingRoom room = findById(roomId);

        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        List<RoomTimeSlot> slots = buildTimeSlots(roomId, targetDate, nowTime);

        List<Meeting> dayMeetings = meetingRepo.findByRoomAndDate(roomId, targetDate);

        List<RoomBookingSlot> bookings = dayMeetings.stream()
                .map(m -> {
                    boolean isNow =
                            targetDate.equals(LocalDate.now())
                                    && m.getStartTime() != null
                                    && m.getEndTime() != null
                                    && !nowTime.isBefore(m.getStartTime())
                                    && nowTime.isBefore(m.getEndTime());

                    return RoomBookingSlot.builder()
                            .meetingId(m.getMeetingId())
                            .meetingTitle(m.getTitle())
                            .organizerName(
                                    m.getOrganizer() != null
                                            ? m.getOrganizer().getUserNameKh() : "")
                            .date(targetDate)
                            .startTime(m.getStartTime())
                            .endTime(m.getEndTime())
                            .statusCode(m.getStatusCode().getStatusCode())
                            .statusLabel(m.getStatusCode().getLabelKh())
                            .isNow(isNow)
                            .isPast(false)
                            .isCancelled(
                                    "CANCELLED".equals(m.getStatusCode().getStatusCode()))
                            .build();
                })
                .toList();

        return RoomScheduleResponse.builder()
                .roomId(roomId)
                .roomCode(room.getRoomCode())
                .status(room.getStatus())
                .statusLabel(room.getStatus().getLabelKh())
                .bookings(bookings)
                .todaySlots(slots)
                .build();
    }

    @Override
    public MeetingRoomResponse create(MeetingRoomRequest request) {

        if (roomRepo.existsByRoomCode(request.getRoomCode())) {
            throw new DuplicateResourceException(
                    "Code [" + request.getRoomCode() + "] មានស្ទួន");
        }

        MeetingRoom room = roomMapper.toEntity(request);
        room.setStatus(MeetingRoomStatus.AVAILABLE);

        MeetingRoom saved = roomRepo.save(room);

        activityLogService.log(
                "CREATE", "MeetingRoom", saved.getRoomId(),
                "បង្កើត: " + saved.getRoomCode(), buildContext());

        return roomMapper.toResponse(saved);
    }

    @Override
    public MeetingRoomResponse update(Integer id, MeetingRoomRequest request) {

        MeetingRoom room = findById(id);

        if (request.getStatus() == MeetingRoomStatus.IN_USE) {
            throw new BusinessException(
                    "មិនអាចកំណត់ស្ថានភាព «កំពុងប្រើប្រាស់» ដោយផ្ទាល់បានឡើយ។ ស្ថានភាពនេះនឹងត្រូវផ្លាស់ប្តូរដោយស្វ័យប្រវត្តតាមប្រព័ន្ធ នៅពេលកិច្ចប្រជុំចាប់ផ្តើមដំណើរការ។");
        }

        if (request.getStatus() == MeetingRoomStatus.MAINTENANCE
                || request.getStatus() == MeetingRoomStatus.CLOSED) {
            validateNoActiveMeetings(id);
        }

        roomMapper.updateEntity(request, room);

        activityLogService.log(
                "UPDATE", "MeetingRoom", id,
                "កែប្រែ: " + room.getRoomCode(), buildContext());

        return toResponseWithImage(roomRepo.save(room));
    }

    @Override
    public MeetingRoomResponse updateStatus(Integer id, MeetingRoomStatus newStatus) {

        MeetingRoom room = findById(id);

        if (newStatus == MeetingRoomStatus.IN_USE) {
            throw new BusinessException(
                    "មិនអាចកំណត់ស្ថានភាព «កំពុងប្រើប្រាស់» ដោយផ្ទាល់បានឡើយ។ ស្ថានភាពនេះនឹងត្រូវផ្លាស់ប្តូរដោយស្វ័យប្រវត្តតាមប្រព័ន្ធ នៅពេលកិច្ចប្រជុំចាប់ផ្តើមដំណើរការ។"
            );}

        if (newStatus == MeetingRoomStatus.MAINTENANCE
                || newStatus == MeetingRoomStatus.CLOSED) {
            validateNoActiveMeetings(id);
        }

        MeetingRoomStatus oldStatus = room.getStatus();
        room.setStatus(newStatus);

        if (newStatus == MeetingRoomStatus.AVAILABLE) {
            room.setCurrentMeeting(null);
        }

        roomRepo.save(room);

        activityLogService.log(
                "UPDATE", "MeetingRoom", id,
                "ស្ថានភាព: " + oldStatus.getLabelKh() + " ទៅ " + newStatus.getLabelKh(),
                buildContext());

        return toResponseWithImage(room);
    }

    @Override
    public void delete(Integer id) {

        MeetingRoom room = findById(id);

        validateNoActiveMeetings(id);

        if (room.getStatus() == MeetingRoomStatus.IN_USE) {
            throw new BusinessException(
                    "មិនអាចលុបបន្ទប់ប្រជុំដែលកំពុងស្ថិតក្នុងស្ថានភាព «កំពុងប្រើប្រាស់» បានឡើយ។ សូមរង់ចាំរហូតដល់កិច្ចប្រជុំត្រូវបានបញ្ចប់។");
        }

        if (room.getAttachment() != null) {
            Integer attachmentId = room.getAttachment().getAttachmentId();
            room.setAttachment(null);
            roomRepo.save(room);
            attachmentService.delete(attachmentId);
        }

        roomRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "MeetingRoom", id,
                "លុបបន្ទប់ប្រជុំ៖ " + room.getRoomCode(), buildContext());
    }


    //Me-cl
    @Override
    public MeetingRoomResponse uploadRoomImage(Integer id, MultipartFile file) {

        validateImageFile(file);

        MeetingRoom room = findById(id);

        if (room.getAttachment() != null) {
            Integer oldAttachmentId = room.getAttachment().getAttachmentId();
            room.setAttachment(null);
            roomRepo.save(room);
            attachmentService.delete(oldAttachmentId);
        }

        var uploaded = attachmentService.upload(
                file,
                gov.kh.mcr.inspectorate.enums.AttachmentRefType.MEETING_ROOM,
                id);

        MeetingRoom updatedRoom = attachmentRepo.findById(uploaded.getAttachmentId())
                .map(att -> {
                    room.setAttachment(att);
                    return roomRepo.save(room);
                })
                .orElse(room);

        activityLogService.log(
                "UPDATE", "MeetingRoom", id,
                "Upload រូបភាព: " + room.getRoomCode(), buildContext());

        return toResponseWithImage(updatedRoom);
    }


    //Me-cl
    @Override
    public MeetingRoomResponse removeImage(Integer id) {

        MeetingRoom room = findById(id);

        if (room.getAttachment() == null) {
            throw new BusinessException("បន្ទប់នេះមិនមានរូបភាពទេ");
        }

        Integer attachmentId = room.getAttachment().getAttachmentId();

        room.setAttachment(null);
        MeetingRoom saved = roomRepo.save(room);

        attachmentService.delete(attachmentId);

        activityLogService.log(
                "UPDATE", "MeetingRoom", id,
                "លុបរូបភាព: " + room.getRoomCode(), buildContext());

        return roomMapper.toResponse(saved);
    }

    private MeetingRoomResponse toResponseWithImage(MeetingRoom room) {
        MeetingRoomResponse resp = roomMapper.toResponse(room);
        if (room.getAttachment() != null) {
            resp.setImageUrl(
                    minioService.getPresignedUrl(
                            room.getAttachment().getFilePath()));
        }
        return resp;
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("សូមជ្រើសរើសរូបភាពប្រវត្តិរូបមកជាមួយដើម្បីបន្ត។");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException(
                    "ប្រភេទឯកសារនេះមិនត្រូវបានអនុញ្ញាតក្នុងប្រព័ន្ធឡើយ។ សូមប្រើប្រាស់ទម្រង់ឯកសាររូបភាពជាប្រភេទ JPG, PNG ឬ WEBP ប៉ុណ្ណោះ។");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            long currentMb = file.getSize() / 1024 / 1024;
            throw new BusinessException("ទំហំរូបភាពធំលើសការកំណត់។ ប្រព័ន្ធអនុញ្ញាតឱ្យបង្ហោះរូបភាពដែលមានទំហំអតិបរមា 5MB ប៉ុណ្ណោះ (ទំហំបច្ចុប្បន្ន៖ " + currentMb + "MB)។");
        }
    }

    private List<RoomTimeSlot> buildTimeSlots(
            Integer roomId, LocalDate date, LocalTime nowTime) {

        List<Meeting> dayMeetings = meetingRepo.findByRoomAndDate(roomId, date);

        boolean isToday = date.equals(LocalDate.now());

        List<RoomTimeSlot> slots = new ArrayList<>();

        LocalTime slot = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(18, 0);

        while (slot.isBefore(end)) {
            LocalTime slotEnd = slot.plusMinutes(30);

            LocalTime finalSlot = slot;

            Meeting bookedMeeting = dayMeetings.stream()
                    .filter(m ->
                            !m.getStartTime().isAfter(finalSlot)
                                    && m.getEndTime().isAfter(finalSlot))
                    .findFirst()
                    .orElse(null);

            String slotStatus;
            String slotLabel;
            Integer meetingId = null;
            String meetingTitle = null;

            if (bookedMeeting != null) {
                boolean isNow =
                        isToday
                                && !nowTime.isBefore(bookedMeeting.getStartTime())
                                && nowTime.isBefore(bookedMeeting.getEndTime());

                slotStatus = isNow ? "IN_USE" : "BOOKED";
                slotLabel = isNow ? "កំពុងប្រជុំ" : "កក់ហើយ";
                meetingId = bookedMeeting.getMeetingId();
                meetingTitle = bookedMeeting.getTitle();
            } else {
                slotStatus = "AVAILABLE";
                slotLabel = "ទំនេរ";
            }

            slots.add(
                    RoomTimeSlot.builder()
                            .date(date)
                            .startTime(slot)
                            .endTime(slotEnd)
                            .slotStatus(slotStatus)
                            .slotStatusLabel(slotLabel)
                            .meetingId(meetingId)
                            .meetingTitle(meetingTitle)
                            .build());

            slot = slotEnd;
        }

        return slots;
    }

    private void validateNoActiveMeetings(Integer roomId) {

        List<Meeting> upcoming = meetingRepo.findByRoomSchedule(
                roomId, LocalDate.now(), LocalDate.now().plusDays(30));

        long active = upcoming.stream()
                .filter(m ->
                        !"CANCELLED".equals(m.getStatusCode().getStatusCode())
                                && !"COMPLETED".equals(m.getStatusCode().getStatusCode()))
                .count();

        if (active > 0) {
            throw new BusinessException(
                    "មិនអាចដំណើរការបានឡើយ ព្រោះបន្ទប់ប្រជុំនេះមានកិច្ចប្រជុំដែលបានរៀបចំទុកជាមុន (Scheduled) ចំនួន "
                            + active + " កិច្ចប្រជុំ។ សូមមេត្តាលុបចោល ឬផ្លាស់ប្តូរកាលវិភាគកិច្ចប្រជុំទាំងនោះជាមុនសិន។"
            );
        }
    }

    private MeetingRoom findById(Integer id) {
        return roomRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("បន្ទប់ប្រជុំ", id));
    }

    private ActivityLogContext buildContext() {
        try {
            var req = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes())
                    .getRequest();
            return securityUtils.buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext.builder().build();
        }
    }
}