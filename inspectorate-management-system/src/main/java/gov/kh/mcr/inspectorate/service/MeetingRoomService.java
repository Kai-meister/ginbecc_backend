package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.MeetingRoomRequest;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.dto.response.MeetingRoomResponse;
import gov.kh.mcr.inspectorate.enums.RoomStatus;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface MeetingRoomService {

    List<MeetingRoomResponse> getAll(
            RoomStatus status);

    MeetingRoomResponse getById(Integer id);

    MeetingRoomResponse create(
            MeetingRoomRequest request);

    MeetingRoomResponse update(
            Integer id, MeetingRoomRequest request);

    void delete(Integer id);

    AttachmentResponse uploadImage(
            Integer roomId, MultipartFile file);
}