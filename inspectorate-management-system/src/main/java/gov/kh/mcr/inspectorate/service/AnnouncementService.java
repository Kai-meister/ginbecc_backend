package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request
        .AnnouncementRequest;
import gov.kh.mcr.inspectorate.dto.response
        .AnnouncementReadStatusResponse;
import gov.kh.mcr.inspectorate.dto.response
        .AnnouncementResponse;
import gov.kh.mcr.inspectorate.dto.response
        .PageResponse;
import org.springframework.web.multipart
        .MultipartFile;

public interface AnnouncementService {

    PageResponse<AnnouncementResponse> getAll(
            int page, int size,
            String status, String priority,Boolean includeExpired);

    AnnouncementResponse getById(Integer id);

    AnnouncementReadStatusResponse
    getReadStatus(Integer id);

    AnnouncementResponse create(
            AnnouncementRequest request);

    AnnouncementResponse update(
            Integer id,
            AnnouncementRequest request);

    AnnouncementResponse uploadAttachment(
            Integer announcementId,
            MultipartFile file);

    String getAttachmentUrl(
            Integer announcementId);

    void markAsRead(Integer announcementId);

    void delete(Integer id);
}