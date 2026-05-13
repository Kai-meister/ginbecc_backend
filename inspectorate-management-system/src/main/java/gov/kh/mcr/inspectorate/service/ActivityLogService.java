package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.ActivityLog;

import java.time.LocalDateTime;

public interface ActivityLogService {

    void log(String action, String entityType,
             Integer entityId, String details);


    PageResponse<ActivityLog> getLogs(
            Integer userId,
            String action,
            String entityType,
            LocalDateTime from,
            LocalDateTime to,
            int page, int size);


    ActivityLog getById(Integer id);
}