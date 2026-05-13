package gov.kh.mcr.inspectorate.service.impl;

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
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import gov.kh.mcr.inspectorate.service.MeetingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingRoomServiceImpl implements MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;
    private final AttachmentRepository attachmentRepository;
    private final MeetingRoomMapper meetingRoomMapper;
    private final ActivityLogService activityLogService;
    private final AttachmentService  attachmentService;

    @Override
    @Transactional(readOnly = true)
    public List<MeetingRoomResponse> getAll(RoomStatus status) {

        List<MeetingRoom> list = status != null
                ? meetingRoomRepository.findByStatus(status)
                : meetingRoomRepository
                  .findAllByOrderByRoomCodeAsc();

        return list.stream()
                .map(meetingRoomMapper::toResponse)
                .toList();
    }

    @Override
    public AttachmentResponse uploadImage(
            Integer roomId, MultipartFile file) {

        findById(roomId); // validate exists

        AttachmentResponse response =
                attachmentService.upload(
                        file, AttachmentRefType.MEETING_ROOM.getCode(), roomId);

        // Update room.imagePath
        MeetingRoom room = findById(roomId);
        attachmentRepository
                .findById(response.getAttachmentId())
                .ifPresent(room::setImagePath);
        meetingRoomRepository.save(room);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public MeetingRoomResponse getById(Integer id) {
        return meetingRoomMapper.toResponse(findById(id));
    }

    @Override
    public MeetingRoomResponse create(MeetingRoomRequest request) {
        if (meetingRoomRepository.existsByRoomCode(
                request.getRoomCode())) {
            throw new DuplicateResourceException(
                    "កូដបន្ទប់ [" + request.getRoomCode()
                            + "] មានស្ទួន");
        }

        MeetingRoom room = meetingRoomMapper.toEntity(request);

        if (request.getImageAttachmentId() != null) {
            attachmentRepository
                    .findById(request.getImageAttachmentId())
                    .ifPresent(room::setImagePath);
        }

        MeetingRoom saved = meetingRoomRepository.save(room);

        activityLogService.log("CREATE", "MeetingRoom",
                saved.getRoomId(),
                "បង្កើតបន្ទប់: " + saved.getRoomCode());

        return meetingRoomMapper.toResponse(saved);
    }

    @Override
    public MeetingRoomResponse update(
            Integer id, MeetingRoomRequest request) {
        MeetingRoom room = findById(id);
        meetingRoomMapper.updateEntity(request, room);

        if (request.getImageAttachmentId() != null) {
            attachmentRepository
                    .findById(request.getImageAttachmentId())
                    .ifPresent(room::setImagePath);
        }

        activityLogService.log("UPDATE", "MeetingRoom",
                id, "កែប្រែ: " + room.getRoomCode());

        return meetingRoomMapper.toResponse(
                meetingRoomRepository.save(room));
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        meetingRoomRepository.deleteById(id);
        activityLogService.log("DELETE", "MeetingRoom",
                id, "លុបបន្ទប់ប្រជុំ");
    }

    private MeetingRoom findById(Integer id) {
        return meetingRoomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("បន្ទប់ប្រជុំ", id));
    }
}