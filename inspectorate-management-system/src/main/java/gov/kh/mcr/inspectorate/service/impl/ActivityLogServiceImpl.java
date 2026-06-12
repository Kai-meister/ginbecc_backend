package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.response.ActivityLogResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.ActivityLog;
import gov.kh.mcr.inspectorate.entity.User;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.ActivityLogMapper;
import gov.kh.mcr.inspectorate.repository.ActivityLogRepository;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ActivityLogServiceImpl
        implements ActivityLogService {

    private final ActivityLogRepository logRepo;
    private final UserRepository userRepo;
    private final ActivityLogMapper logMapper;

    // log() with explicit Context
    // Context extract ពី main thread
    // Pass ទៅ @Async thread
    @Override
    @Async
    @Transactional(
            propagation = Propagation.REQUIRES_NEW)
    public void log(
            String action,
            String entityType,
            Integer entityId,
            String details,
            ActivityLogContext context) {

        try {
            ActivityLog.ActivityLogBuilder builder =
                    ActivityLog.builder()
                            .action(action)
                            .entityType(entityType)
                            .entityId(entityId)
                            .details(details);
            if (context != null) {

                builder
                        .ipAddress(context.getIpAddress())
                        .userAgent(context.getUserAgent())
                        .userEmail(context.getUserEmail());

                if (context.getUserId() != null) {
                    userRepo.findById(
                                    context.getUserId())
                            .ifPresent(builder::user);
                }
            }

            logRepo.save(builder.build());

        } catch (Exception ex) {
            log.error(
                    "ការរក្សាទុកប្រវត្តិនៃសកម្មភាព បានបរាជ័យ៖ {}",
                    ex.getMessage());
        }
    }


    // log() auto-detect from SecurityContext
    // ប្រើក្នុង @Async method ខ្លួនឯង
    // SecurityContext propagate បាន
    // (InheritableThreadLocal mode)
    @Override
    @Async
    @Transactional(
            propagation = Propagation.REQUIRES_NEW)
    public void log(
            String action,
            String entityType,
            Integer entityId,
            String details) {

        try {
            Authentication auth =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (auth == null
                    || !auth.isAuthenticated()
                    || "anonymousUser".equals(
                    auth.getPrincipal())) {

                logRepo.save(
                        ActivityLog.builder()
                                .action(action)
                                .entityType(entityType)
                                .entityId(entityId)
                                .details(details)
                                .userEmail("SYSTEM")
                                .build());
                return;
            }

            String email = auth.getName();

            Optional<User> userOpt =
                    userRepo.findByEmail(email);

            ActivityLog.ActivityLogBuilder builder =
                    ActivityLog.builder()
                            .action(action)
                            .entityType(entityType)
                            .entityId(entityId)
                            .details(details)
                            .userEmail(email);

            userOpt.ifPresent(builder::user);

            logRepo.save(builder.build());

        } catch (Exception ex) {
            log.error(
                    "ការរក្សាទុកប្រវត្តិនៃសកម្មភាព បានបរាជ័យ៖ {}",
                    ex.getMessage());
        }
    }

    // GET LOGS
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityLogResponse>
    getLogs(
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
                logRepo.findWithFilters(
                                userId, action,
                                entityType, from, to,
                                pageable)
                        .map(logMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityLogResponse getById(Integer id) {
        return logMapper.toResponse(
                logRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Log", id)));
    }
}