package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.MeetingRoomRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingRoomResponse;
import gov.kh.mcr.inspectorate.dto.response.RoomScheduleResponse;
import gov.kh.mcr.inspectorate.enums.MeetingRoomStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface MeetingRoomService {

    List<MeetingRoomResponse> getAll(MeetingRoomStatus status);

    MeetingRoomResponse getById(Integer id);

    RoomScheduleResponse getSchedule(Integer roomId, LocalDate from, LocalDate to);

    RoomScheduleResponse getAvailability(Integer roomId, LocalDate date);

    MeetingRoomResponse create(MeetingRoomRequest request);

    MeetingRoomResponse update(Integer id, MeetingRoomRequest request);

    MeetingRoomResponse updateStatus(Integer id, MeetingRoomStatus newStatus);

    void delete(Integer id);

    MeetingRoomResponse removeImage(Integer id);

    MeetingRoomResponse uploadRoomImage(Integer id, MultipartFile file);
}