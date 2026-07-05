package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.MeetingRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import java.time.LocalDate;
import java.util.List;

public interface MeetingService {

    PageResponse<MeetingResponse> getAll(
            int page, int size,
            String status, Integer roomId);

    MeetingResponse getById(Integer id);

    List<MeetingResponse> getCalendar(
            int month, int year);

    List<MeetingResponse> getRoomSchedule(
            Integer roomId, LocalDate date);

    MeetingResponse create(MeetingRequest request);

    MeetingResponse update(
            Integer id, MeetingRequest request);
    MeetingResponse updateStatus(
            Integer id, String newStatusCode);
    void delete(Integer id);
}