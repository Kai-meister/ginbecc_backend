package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request.MeetingRoomRequest;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.dto.response.MeetingRoomResponse;
import gov.kh.mcr.inspectorate.entity.MeetingRoom;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.enums.RoomStatus;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.MeetingRoomMapper;
import gov.kh.mcr.inspectorate.repository.AttachmentRepository;
import gov.kh.mcr.inspectorate.repository.MeetingRoomRepository;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import gov.kh.mcr.inspectorate.service.MeetingRoomService;
import gov.kh.mcr.inspectorate.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingRoomServiceImpl
        implements MeetingRoomService {

    private final MeetingRoomRepository roomRepo;
    private final AttachmentRepository attachRepo;
    private final MeetingRoomMapper roomMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;
    private final AttachmentService attachmentService;
    private final MinioService minioService;

    // ─────────────────────────────────────────────
    // GET ALL — filter by status
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<MeetingRoomResponse> getAll(
            RoomStatus status) {

        List<MeetingRoom> list = status != null
                ? roomRepo.findByStatus(status)
                : roomRepo.findAllByOrderByRoomCodeAsc();

        return list.stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MeetingRoomResponse getById(Integer id) {
        return roomMapper.toResponse(findById(id));
    }

    // ─────────────────────────────────────────────
    // CREATE — Fix: គ្មាន imageAttachmentId
    // ─────────────────────────────────────────────
    @Override
    public MeetingRoomResponse create(
            MeetingRoomRequest request) {

        if (roomRepo.existsByRoomCode(
                request.getRoomCode())) {
            throw new DuplicateResourceException(
                    "មិនអាចបង្កើតបានឡើយ ដោយសារកូដបន្ទប់ប្រជុំ «"
                            + request.getRoomCode()
                            + "» នេះមានក្នុងប្រព័ន្ធរួចហើយ។");
        }

        MeetingRoom room =
                roomMapper.toEntity(request);
        room.setAttachment(null);

        MeetingRoom saved = roomRepo.save(room);

        activityLogService.log(
                "CREATE", "MeetingRoom",
                saved.getRoomId(),
                "បង្កើតបន្ទប់ប្រជុំថ្មី កូដ "
                        + saved.getRoomCode(),
                buildContext());

        return roomMapper.toResponse(saved);
    }

    // ─────────────────────────────────────────────
    // UPDATE — Fix: គ្មាន imageAttachmentId
    // ─────────────────────────────────────────────
    @Override
    public MeetingRoomResponse update(
            Integer id,
            MeetingRoomRequest request) {

        MeetingRoom room = findById(id);
        roomMapper.updateEntity(request, room);

        activityLogService.log(
                "UPDATE", "MeetingRoom",
                id, "កែប្រែព័ត៌មានបន្ទប់ប្រជុំ កូដ " + room.getRoomCode(),
                buildContext());

        return roomMapper.toResponse(
                roomRepo.save(room));
    }

    // ─────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────
    @Override
    public void delete(Integer id) {
        MeetingRoom room = findById(id);
        roomRepo.deleteById(id);
        activityLogService.log(
                "DELETE", "MeetingRoom",
                id, "លុបទិន្នន័យបន្ទប់ប្រជុំ កូដ៖ " + room.getRoomCode(),
                buildContext());
    }

    @Override
    public MeetingRoomResponse uploadImage(
            Integer roomId,
            MultipartFile file) {

        MeetingRoom room = findById(roomId);
        AttachmentResponse resp =
                attachmentService.upload(
                        file,
                        AttachmentRefType.MEETING_ROOM,
                        roomId);
        attachRepo.findById(
                        resp.getAttachmentId())
                .ifPresent(att -> {
                    room.setAttachment(att);
                    roomRepo.save(room);
                });

        activityLogService.log(
                "UPDATE", "MeetingRoom",
                roomId,
                "ផ្ទុកឡើងរូបភាពបន្ទប់ប្រជុំ "
                        + resp.getOriginalName(),
                buildContext());

        return roomMapper.toResponse(
                roomRepo.save(room));
    }

    @Override
    public MeetingRoomResponse removeImage(
            Integer roomId) {

        MeetingRoom room = findById(roomId);

        if (room.getAttachment() == null) {
            return roomMapper.toResponse(room);
        }

        attachmentService.delete(
                room.getAttachment().getAttachmentId());

        room.setAttachment(null);

        activityLogService.log(
                "UPDATE", "MeetingRoom",
                roomId, "លុបរូបភាពបន្ទប់ប្រជុំ កូដ៖ " + room.getRoomCode(),
                buildContext());

        return roomMapper.toResponse(
                roomRepo.save(room));
    }

    @Override
    @Transactional(readOnly = true)
    public String getImageUrl(Integer roomId) {
        MeetingRoom room = findById(roomId);

        if (room.getAttachment() == null) {
            return null;
        }

        return minioService.getPresignedUrl(
                room.getAttachment().getFilePath());
    }

    // ── Private helpers ───────────────────────────
    private MeetingRoom findById(Integer id) {
        return roomRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យបន្ទប់ប្រជុំដែលមានលេខសម្គាល់", id));
    }

    private ActivityLogContext buildContext() {
        try {
            var req = ((ServletRequestAttributes)
                    RequestContextHolder
                            .currentRequestAttributes())
                    .getRequest();
            return securityUtils.buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext.builder()
                    .build();
        }
    }
}