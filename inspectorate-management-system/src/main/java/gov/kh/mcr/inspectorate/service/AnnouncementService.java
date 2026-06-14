package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request
        .AnnouncementRequest;
import gov.kh.mcr.inspectorate.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

public interface AnnouncementService {

    PageResponse<AnnouncementResponse> getAll(
            int page, int size,
            String status, String priority);

    AnnouncementResponse getById(Integer id);

    AnnouncementReadStatusResponse getReadStatus(
            Integer id);

    AnnouncementResponse create(
            AnnouncementRequest request);

    AnnouncementResponse update(
            Integer id,
            AnnouncementRequest request);

    // currentOfficerId for owner check
    void markAsRead(
            Integer announcementId,
            Integer currentOfficerId);

    void delete(Integer id);

    AnnouncementResponse uploadAttachment(
            Integer announcementId,
            MultipartFile file);

    String getAttachmentUrl(
            Integer announcementId);
}