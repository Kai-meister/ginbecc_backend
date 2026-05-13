package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.ActivityLog;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.repository.ActivityLogRepository;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    @Override
    @Async
    public void log(String action, String entityType,
                    Integer entityId, String details) {
        try {
            var auth = SecurityContextHolder
                    .getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()
                    || "anonymousUser".equals(auth.getPrincipal())) {
                return;
            }
            userRepository.findByEmail(auth.getName())
                    .ifPresent(user ->
                            activityLogRepository.save(
                                    ActivityLog.builder()
                                            .user(user)
                                            .action(action)
                                            .entityType(entityType)
                                            .entityId(entityId)
                                            .details(details)
                                            .build()));
        } catch (Exception ex) {
            log.warn("ActivityLog error: {}", ex.getMessage());
        }
    }

    @Override
    public ActivityLog getById(Integer id) {
        return activityLogRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Log", id));
    }
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityLog> getLogs(
            Integer userId,
            String action,
            String entityType,
            LocalDateTime from,
            LocalDateTime to,
            int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        return PageResponse.of(
                activityLogRepository.findWithFilters(
                        userId, action, entityType, from, to,
                        pageable));
    }
}