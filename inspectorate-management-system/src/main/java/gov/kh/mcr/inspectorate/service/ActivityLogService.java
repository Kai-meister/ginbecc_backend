package gov.kh.mcr.inspectorate.service;
import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.response.ActivityLogResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;

import java.time.LocalDateTime;

public interface ActivityLogService {


    // ប្រើពី Service (Controller inject context)
    void log(String action,
             String entityType,
             Integer entityId,
             String details,
             ActivityLogContext context);


    // ប្រើពី Service ដែល SecurityContext OK
    void log(String action,
             String entityType,
             Integer entityId,
             String details);


    PageResponse<ActivityLogResponse> getLogs(
            Integer userId,
            String action,
            String entityType,
            LocalDateTime from,
            LocalDateTime to,
            int page, int size);

    ActivityLogResponse getById(Integer id);
}