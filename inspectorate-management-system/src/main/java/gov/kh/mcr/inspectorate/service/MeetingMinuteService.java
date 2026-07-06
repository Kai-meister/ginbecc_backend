package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.MeetingMinuteRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingMinuteResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface MeetingMinuteService {

    PageResponse<MeetingMinuteResponse> getAll(
            int page, int size, Integer meetingId);

    MeetingMinuteResponse getById(Integer id);

    MeetingMinuteResponse create(
            MeetingMinuteRequest request);

    MeetingMinuteResponse update(
            Integer id, MeetingMinuteRequest request);
    MeetingMinuteResponse uploadAttachment(
            Integer minuteId, MultipartFile file);

    String getDownloadUrl(Integer minuteId);
}