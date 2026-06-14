package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request
        .NotificationCreateRequest;
import gov.kh.mcr.inspectorate.dto.response
        .NotificationResponse;
import gov.kh.mcr.inspectorate.dto.response
        .PageResponse;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper
        .NotificationMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.service
        .NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation
        .Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Propagation;
import org.springframework.transaction.annotation
        .Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notifRepo;
    private final UserRepository         userRepo;
    private final NotificationMapper     notifMapper;

    @Override
    public NotificationResponse create(
            NotificationCreateRequest req) {

        validateTarget(req);
        User user = resolveUser(req);

        Notification saved =
                notifRepo.save(build(user, req));

        log.info("Notification: [{}] → {}",
                req.getType(), user.getEmail());

        return notifMapper.toResponse(saved);
    }

    @Override
    public List<NotificationResponse> createBulk(
            NotificationCreateRequest req) {

        if (req.getOfficerIds() == null
                || req.getOfficerIds().isEmpty()) {
            throw new BusinessException(
                    "បញ្ជីលេខសម្គាល់មន្ត្រី (officerIds) គឺចាំបាច់ត្រូវតែមាន។");
        }

        List<NotificationResponse> results =
                new ArrayList<>();

        req.getOfficerIds().forEach(oid -> {
            try {
                userRepo
                        .findByOfficer_OfficerId(oid)
                        .ifPresentOrElse(
                                u -> results.add(
                                        notifMapper.toResponse(
                                                notifRepo.save(
                                                        build(u, req)))),
                                () -> log.warn(
                                        "No user officer: {}",
                                        oid));
            } catch (Exception ex) {
                // Log but continue other officers
                log.error(
                        "Bulk notif officer {}: {}",
                        oid, ex.getMessage());
            }
        });

        log.info("Bulk: {}/{} sent",
                results.size(),
                req.getOfficerIds().size());

        return results;
    }

    @Override
    @Async
    @Transactional(
            propagation = Propagation.REQUIRES_NEW)
    public void createByOfficerId(
            Integer officerId,
            String title,
            String message,
            NotificationType type,
            Integer referenceId) {

        try {
            userRepo
                    .findByOfficer_OfficerId(officerId)
                    .ifPresentOrElse(
                            u -> save(u, title,
                                    message, type,
                                    referenceId),
                            // Fix — log warn not silent
                            () -> log.warn(
                                    "createByOfficerId:"
                                            + " No user for"
                                            + " officer={}",
                                    officerId));
        } catch (Exception ex) {
            log.error(
                    "createByOfficerId failed"
                            + " officer={}: {}",
                    officerId, ex.getMessage());
        }
    }

    @Override
    @Async
    @Transactional(
            propagation = Propagation.REQUIRES_NEW)
    public void createByUserId(
            Integer userId,
            String title,
            String message,
            NotificationType type,
            Integer referenceId) {

        try {
            userRepo.findById(userId)
                    .ifPresentOrElse(
                            u -> save(u, title,
                                    message, type,
                                    referenceId),
                            () -> log.warn(
                                    "createByUserId:"
                                            + " No user={}",
                                    userId));
        } catch (Exception ex) {
            log.error(
                    "createByUserId failed"
                            + " user={}: {}",
                    userId, ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse>
    getMyNotifications(
            Integer currentUserId,
            Boolean isRead,
            int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<Notification> result =
                isRead != null
                        ? notifRepo
                          .findByUser_UserIdAndIsRead(
                                  currentUserId,
                                  isRead, pageable)
                        : notifRepo.findByUser_UserId(
                        currentUserId, pageable);

        return PageResponse.of(
                result.map(notifMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getById(
            Integer notificationId,
            Integer currentUserId) {

        Notification n = notifRepo
                .findByNotificationIdAndUser_UserId(
                        notificationId, currentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យសេចក្តីជូនដំណឹងដែលមានលេខសម្គាល់ ",
                                notificationId));

        return notifMapper.toResponse(n);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(
            Integer currentUserId) {
        return notifRepo
                .countByUser_UserIdAndIsRead(
                        currentUserId, false);
    }

    @Override
    public void markAsRead(
            Integer notificationId,
            Integer currentUserId) {

        Notification n = notifRepo
                .findByNotificationIdAndUser_UserId(
                        notificationId, currentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យសេចក្តីជូនដំណឹងដែលមានលេខសម្គាល់ ",
                                notificationId));

        if (Boolean.TRUE.equals(n.getIsRead())) {
            return;
        }

        n.setIsRead(true);
        n.setReadAt(LocalDateTime.now());
        notifRepo.save(n);
    }

    @Override
    public int markAllAsRead(
            Integer currentUserId) {
        return notifRepo.markAllAsRead(
                currentUserId,
                LocalDateTime.now());
    }

    private void validateTarget(
            NotificationCreateRequest req) {

        int count =
                (req.getOfficerId() != null ? 1 : 0)
                        + (req.getUserId()    != null ? 1 : 0)
                        + (req.getOfficerIds() != null
                        && !req.getOfficerIds().isEmpty()
                        ? 1 : 0);

        if (count == 0) {
            throw new BusinessException(
                    "សូមកំណត់គោលដៅទទួលសេចក្តីជូនដំណឹងណាមួយដូចជា៖ បើផ្ញើឱ្យមន្ត្រីម្នាក់, បើផ្ញើឱ្យគណនីអ្នកប្រើប្រាស់ម្នាក់ ឬ បើផ្ញើឱ្យមន្ត្រីច្រើននាក់ជាក្រុម។");
        }
        if (count > 1) {
            throw new BusinessException(
                    "មិនអាចកំណត់គោលដៅទទួលច្រើនទម្រង់ក្នុងពេលតែមួយបានឡើយ។ សូមជ្រើសរើសយកតែជម្រើសមួយគត់ (តែ ១)។");
        }
    }

    private User resolveUser(
            NotificationCreateRequest req) {

        if (req.getUserId() != null) {
            return userRepo
                    .findById(req.getUserId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "មិនមានទិន្នន័យអ្នកប្រើប្រាស់ដែលមានលេខសម្គាល់៖ ", req.getUserId()));
        }
        if (req.getOfficerId() != null) {
            return userRepo
                    .findByOfficer_OfficerId(
                            req.getOfficerId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "មិនមានគណនីអ្នកប្រើប្រាស់ដែលភ្ជាប់ជាមួយលេខសម្គាល់មន្ត្រី៖ ",
                                    req.getOfficerId()));
        }
        throw new BusinessException(
                "មិនអាចកំណត់អត្តសញ្ញាណអ្នកប្រើប្រាស់បានឡើយ ដោយសារខ្វះព័ត៌មានគោលដៅទទួល។");
    }

    private Notification build(
            User user,
            NotificationCreateRequest req) {
        return Notification.builder()
                .user(user)
                .title(req.getTitle())
                .message(req.getMessage())
                .type(req.getType())
                .referenceId(req.getReferenceId())
                .isRead(false)
                .build();
    }

    private void save(
            User user,
            String title,
            String message,
            NotificationType type,
            Integer referenceId) {
        notifRepo.save(
                Notification.builder()
                        .user(user)
                        .title(title)
                        .message(message)
                        .type(type)
                        .referenceId(referenceId)
                        .isRead(false)
                        .build());
    }
}