package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.response.ActivityLogResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.ActivityLog;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.ActivityLogMapper;
import gov.kh.mcr.inspectorate.repository.ActivityLogRepository;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ActivityLogServiceImpl
        implements ActivityLogService {

    private final ActivityLogRepository logRepository;
    private final UserRepository        userRepository;
    private final ActivityLogMapper logMapper;

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String entityType, Integer entityId, String details) {

        logWithRequest(action, entityType, entityId, details, null, null);
    }
    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logWithRequest(
            String action,
            String entityType,
            Integer entityId,
            String details,
            String ipAddress,
            String userAgent) {

        try {
            var auth = SecurityContextHolder
                    .getContext().getAuthentication();

            if (auth == null
                    || !auth.isAuthenticated()
                    || "anonymousUser".equals(
                    auth.getPrincipal())) {

                // Save without user ref
                logRepository.save(
                        ActivityLog.builder()
                                .action(action)
                                .entityType(entityType)
                                .entityId(entityId)
                                .details(details)
                                .ipAddress(ipAddress)
                                .userAgent(userAgent)
                                .build());
                return;
            }

            userRepository
                    .findByEmail(auth.getName())
                    .ifPresent(user ->
                            logRepository.save(
                                    ActivityLog.builder()
                                            .user(user)
                                            .action(action)
                                            .entityType(entityType)
                                            .entityId(entityId)
                                            .details(details)
                                            .ipAddress(ipAddress)
                                            .userAgent(userAgent)
                                            .build()));

        } catch (Exception ex) {
            log.error(
                    "ActivityLog save failed: {}",
                    ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityLogResponse> getLogs(
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
                logRepository.findWithFilters(
                                userId, action,
                                entityType, from, to,
                                pageable)
                        .map(logMapper::toResponse));
    }
    @Override
    @Transactional(readOnly = true)
    public ActivityLogResponse getById(Integer id) {
        return logMapper.toResponse(
                logRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Log", id)));
    }

}