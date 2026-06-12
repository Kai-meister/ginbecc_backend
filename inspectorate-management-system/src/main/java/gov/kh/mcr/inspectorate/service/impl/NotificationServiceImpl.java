package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.NotificationCreateRequest;
import gov.kh.mcr.inspectorate.dto.response.NotificationResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums.NotificationType;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper.NotificationMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation
        .Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import gov.kh.mcr.inspectorate.service.FcmService; //phireak

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl
                implements NotificationService {

        private final NotificationRepository notifRepo;
        private final UserRepository userRepo;
        private final NotificationMapper notifMapper;
        private final FcmService fcmService; // phireak

        // ─────────────────────────────────────────────
        // CREATE — Admin single
        // ─────────────────────────────────────────────
        @Override
        public NotificationResponse create(
                        NotificationCreateRequest req) {

                validateTarget(req);
                User user = resolveUser(req);

                Notification saved = notifRepo.save(build(user, req));

                fcmService.sendToUser( // phireak
                                user.getUserId(), // phireak
                                req.getTitle(), // phireak
                                req.getMessage(), // phireak
                                req.getType(), // phireak
                                req.getReferenceId()); // phireak

                log.info("Notification: [{}] → {}",
                                req.getType(), user.getEmail());

                return notifMapper.toResponse(saved);
        }

        // ─────────────────────────────────────────────
        // CREATE BULK — Admin sync (not async)
        // Fix — Admin bulk ប្រើ sync
        // ─────────────────────────────────────────────
        @Override
        public List<NotificationResponse> createBulk(
                        NotificationCreateRequest req) {

                if (req.getOfficerIds() == null
                                || req.getOfficerIds().isEmpty()) {
                        throw new BusinessException(
                                        "officerIds ចាំបាច់");
                }

                List<NotificationResponse> results = new ArrayList<>();

                req.getOfficerIds().forEach(oid -> {
                        try {
                                userRepo
                                                .findByOfficer_OfficerId(oid)
                                                .ifPresentOrElse(
                                                                u -> { // phireak
                                                                        Notification saved = notifRepo
                                                                                        .save(build(u, req));// phireak
                                                                        results.add(notifMapper.toResponse(saved));// phireak
                                                                        fcmService.sendToUser(// phireak
                                                                                        u.getUserId(), // phireak
                                                                                        req.getTitle(), // phireak
                                                                                        req.getMessage(), // phireak
                                                                                        req.getType(), // phireak
                                                                                        req.getReferenceId());// phireak
                                                                }, // phireak
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

        // ─────────────────────────────────────────────
        // CREATE BY OFFICER — @Async
        // ─────────────────────────────────────────────
        @Override
        @Async
        @Transactional(propagation = Propagation.REQUIRES_NEW)
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

        // ─────────────────────────────────────────────
        // CREATE BY USER — @Async
        // ─────────────────────────────────────────────
        @Override
        @Async
        @Transactional(propagation = Propagation.REQUIRES_NEW)
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

        // ─────────────────────────────────────────────
        // CREATE FOR ALL ACTIVE USERS — @Async broadcast
        // ─────────────────────────────────────────────
        @Override
        @Async
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void createForAllActiveUsers(
                        String title,
                        String message,
                        NotificationType type,
                        Integer referenceId,
                        Integer excludeUserId) {

                try {
                        userRepo.findAll().forEach(u -> {
                                String status = u.getStatusCode() != null
                                                ? u.getStatusCode().getStatusCode()
                                                : "";
                                if (!"ACTIVE".equals(status)) {
                                        return;
                                }
                                if (excludeUserId != null
                                                && excludeUserId.equals(
                                                                u.getUserId())) {
                                        return;
                                }
                                save(u, title, message, type, referenceId);
                        });
                } catch (Exception ex) {
                        log.error(
                                        "createForAllActiveUsers failed: {}",
                                        ex.getMessage());
                }
        }

        // ─────────────────────────────────────────────
        // GET MY NOTIFICATIONS
        // Fix — currentUserId ពី SecurityContext
        // ─────────────────────────────────────────────
        @Override
        @Transactional(readOnly = true)
        public PageResponse<NotificationResponse> getMyNotifications(
                        Integer currentUserId,
                        Boolean isRead,
                        int page, int size) {

                Pageable pageable = PageRequest.of(
                                page, size,
                                Sort.by("createdAt").descending());

                Page<Notification> result = isRead != null
                                ? notifRepo
                                                .findByUser_UserIdAndIsRead(
                                                                currentUserId,
                                                                isRead, pageable)
                                : notifRepo.findByUser_UserId(
                                                currentUserId, pageable);

                return PageResponse.of(
                                result.map(notifMapper::toResponse));
        }

        // ─────────────────────────────────────────────
        // GET BY ID
        // Fix — owner check
        // ─────────────────────────────────────────────
        @Override
        @Transactional(readOnly = true)
        public NotificationResponse getById(
                        Integer notificationId,
                        Integer currentUserId) {

                // Fix — find with owner check
                Notification n = notifRepo
                                .findByNotificationIdAndUser_UserId(
                                                notificationId, currentUserId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "ការជូនដំណឹង",
                                                notificationId));

                return notifMapper.toResponse(n);
        }

        // ─────────────────────────────────────────────
        // UNREAD COUNT
        // ─────────────────────────────────────────────
        @Override
        @Transactional(readOnly = true)
        public long getUnreadCount(
                        Integer currentUserId) {
                return notifRepo
                                .countByUser_UserIdAndIsRead(
                                                currentUserId, false);
        }

        // ─────────────────────────────────────────────
        // MARK AS READ
        // Fix — owner check + set readAt
        // ─────────────────────────────────────────────
        @Override
        public void markAsRead(
                        Integer notificationId,
                        Integer currentUserId) {

                // Fix — find with owner check
                Notification n = notifRepo
                                .findByNotificationIdAndUser_UserId(
                                                notificationId, currentUserId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "ការជូនដំណឹង",
                                                notificationId));

                if (Boolean.TRUE.equals(n.getIsRead())) {
                        return; // Already read
                }

                // Fix — set both
                n.setIsRead(true);
                n.setReadAt(LocalDateTime.now());
                notifRepo.save(n);
        }

        // ─────────────────────────────────────────────
        // MARK ALL AS READ
        // Fix — pass now for readAt
        // ─────────────────────────────────────────────
        @Override
        public int markAllAsRead(
                        Integer currentUserId) {
                return notifRepo.markAllAsRead(
                                currentUserId,
                                LocalDateTime.now());
        }

        // ── Private Helpers ───────────────────────────

        private void validateTarget(
                        NotificationCreateRequest req) {

                int count = (req.getOfficerId() != null ? 1 : 0)
                                + (req.getUserId() != null ? 1 : 0)
                                + (req.getOfficerIds() != null
                                                && !req.getOfficerIds().isEmpty()
                                                                ? 1
                                                                : 0);

                if (count == 0) {
                        throw new BusinessException(
                                        "ត្រូវ specify target:"
                                                        + " officerId / userId"
                                                        + " / officerIds");
                }
                if (count > 1) {
                        throw new BusinessException(
                                        "specify target តែ ១");
                }
        }

        private User resolveUser(
                        NotificationCreateRequest req) {

                if (req.getUserId() != null) {
                        return userRepo
                                        .findById(req.getUserId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "User", req.getUserId()));
                }
                if (req.getOfficerId() != null) {
                        return userRepo
                                        .findByOfficer_OfficerId(
                                                        req.getOfficerId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "User for Officer",
                                                        req.getOfficerId()));
                }
                throw new BusinessException(
                                "មិនអាច resolve User");
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
                fcmService.sendToUser( // phireak
                                user.getUserId(), // phireak
                                title, message, // phireak
                                type, referenceId); // phireak
        }
}