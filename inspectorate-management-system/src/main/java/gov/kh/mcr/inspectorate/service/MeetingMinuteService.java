package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.MeetingMinuteRequest;
import gov.kh.mcr.inspectorate.dto.response.MeetingMinuteResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;

public interface MeetingMinuteService {

    PageResponse<MeetingMinuteResponse> getAll(int page, int size, Integer meetingId);

    MeetingMinuteResponse getById(Integer id);

    MeetingMinuteResponse create(MeetingMinuteRequest request);

    MeetingMinuteResponse update(Integer id, MeetingMinuteRequest request);
}
