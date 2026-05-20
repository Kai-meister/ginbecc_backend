package gov.kh.mcr.inspectorate.service;
import gov.kh.mcr.inspectorate.dto.response.ActivityLogResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;

import java.time.LocalDateTime;

public interface ActivityLogService {

    void log(String action,
             String entityType,
             Integer entityId,
             String details);

    void logWithRequest(
            String action,
            String entityType,
            Integer entityId,
            String details,
            String ipAddress,
            String userAgent);

    PageResponse<ActivityLogResponse> getLogs(
            Integer userId,
            String action,
            String entityType,
            LocalDateTime from,
            LocalDateTime to,
            int page, int size);

    ActivityLogResponse getById(Integer id);
}