package gov.kh.mcr.inspectorate.service;


import gov.kh.mcr.inspectorate.dto.request.AnnouncementRequest;
import gov.kh.mcr.inspectorate.dto.response.AnnouncementReadStatusResponse;
import gov.kh.mcr.inspectorate.dto.response.AnnouncementResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;

public interface AnnouncementService {

    PageResponse<AnnouncementResponse> getAll(
            int page, int size, String status, String priority);

    AnnouncementResponse getById(Integer id);

    AnnouncementReadStatusResponse getReadStatus(Integer id);

    AnnouncementResponse create(AnnouncementRequest request);

    AnnouncementResponse update(Integer id,
                                AnnouncementRequest request);

    void markAsRead(Integer announcementId, Integer officerId);

    void delete(Integer id);
}
