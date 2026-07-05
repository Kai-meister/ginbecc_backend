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
import gov.kh.mcr.inspectorate.resolver
        .NotificationReferenceResolver;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.FcmService;
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

    private final NotificationRepository
            notifRepo;
    private final UserRepository
            userRepo;
    private final NotificationMapper
            notifMapper;

    private final NotificationReferenceResolver
            referenceResolver;
    private final SecurityUtils securityUtils;
    private final FcmService fcmService; // phireak

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse>
    getMyNotifications(
            Integer currentUserId,
            Boolean isRead,
            int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt")
                        .descending());

        Page<Notification> result =
                isRead != null
                        ? notifRepo
                        .findByUser_UserIdAndIsRead(
                                currentUserId,
                                isRead, pageable)
                        : notifRepo.findByUser_UserId(
                        currentUserId, pageable);

        return PageResponse.of(
                result.map(
                        this::toResponseWithReference));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse>
    getMyNotificationsByType(
            Integer currentUserId,
            NotificationType type,
            Boolean isRead,
            int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt")
                        .descending());

        Page<Notification> result;

        if (type != null && isRead != null) {
            result = notifRepo
                    .findByUser_UserIdAndTypeAndIsRead(
                            currentUserId, type,
                            isRead, pageable);
        } else if (type != null) {
            result = notifRepo
                    .findByUser_UserIdAndType(
                            currentUserId, type,
                            pageable);
        } else if (isRead != null) {
            result = notifRepo
                    .findByUser_UserIdAndIsRead(
                            currentUserId,
                            isRead, pageable);
        } else {
            result = notifRepo
                    .findByUser_UserId(
                            currentUserId, pageable);
        }

        return PageResponse.of(
                result.map(
                        this::toResponseWithReference));
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
                                "ការជូនដំណឹង",
                                notificationId));

        return toResponseWithReference(n);
    }
    private NotificationResponse
    toResponseWithReference(
            Notification n) {

        NotificationResponse dto =
                notifMapper.toResponse(n);
        Map<String, Object> refData =
                referenceResolver.resolve(
                        n.getType(),
                        n.getReferenceId());

        dto.setReferenceData(refData);

        return dto;
    }

    @Override
    public NotificationResponse create(
            NotificationCreateRequest req) {

        validateTarget(req);
        User user = resolveUser(req);

        Notification saved =
                notifRepo.save(build(user, req));

        fcmService.sendToUser( // phireak
                user.getUserId(), // phireak
                req.getTitle(), // phireak
                req.getMessage(), // phireak
                req.getType(), // phireak
                req.getReferenceId()); // phireak

        return toResponseWithReference(saved);
    }

    @Override
    public List<NotificationResponse> createBulk(NotificationCreateRequest req) {

        if (req.getUserIds() == null
                || req.getUserIds().isEmpty()) {
            throw new BusinessException(
                    "សូមជ្រើសរើសអ្នកប្រើប្រាស់ ឬមន្ត្រីដែលត្រូវទទួលការជូនដំណឹងយ៉ាងហោចណាស់ម្នាក់។");
        }

        if (!securityUtils
                .canBypassDepartmentScope()) {

            Integer ownDeptId =
                    securityUtils
                            .getCurrentDepartmentId();

            if (ownDeptId != null) {
                for (Integer uid :
                        req.getUserIds()) {

                    User target = userRepo
                            .findById(uid)
                            .orElse(null);

                    if (target == null)
                        continue;

                    Integer targetDeptId =
                            resolveUserDeptId(
                                    target);

                    if (targetDeptId == null
                            || !ownDeptId.equals(
                            targetDeptId)) {
                        throw new
                                BusinessException(
                                "លោកអ្នកមិនមានសិទ្ធិផ្ញើការជូនដំណឹងទៅកាន់មន្ត្រីក្រៅនាយកដ្ឋានរបស់ខ្លួនឡើយ "
                                        + "(មន្ត្រីទទួល៖ «" + target.getUserNameKh() + "» ស្ថិតក្នុងនាយកដ្ឋានផ្សេង)");
                    }
                }
            }
        }

        List<NotificationResponse> results = new ArrayList<>();

        req.getUserIds().forEach(uid -> {
            try {
                userRepo.findById(uid)// phireak
                        .ifPresentOrElse(
                                u -> {
                                    Notification saved = notifRepo.save(build(u, req));
                                    results.add(notifMapper.toResponse(saved));

                                    // Send FCM notification
                                    fcmService.sendToUser(
                                            u.getUserId(),
                                            req.getTitle(),
                                            req.getMessage(),
                                            req.getType(),
                                            req.getReferenceId());
                                },
                                () -> log.warn("No user: {}", uid)
                        );
            } catch (Exception ex) {
                // Log error but continue processing other users
                log.error("Bulk notif user {}: {}", uid, ex.getMessage(), ex);
            }// phireak
        });
        return results;
    }
    private Integer resolveUserDeptId(
            User u) {
        if (u.getOfficer() != null
                && u.getOfficer()
                .getDepartment() != null) {
            return u.getOfficer()
                    .getDepartment()
                    .getDepartmentId();
        }
        if (u.getContractOfficer() != null
                && u.getContractOfficer()
                .getDepartment()
                != null) {
            return u.getContractOfficer()
                    .getDepartment()
                    .getDepartmentId();
        }
        return null;
    }

    @Override
    @Async
    @Transactional(
            propagation =
                    Propagation.REQUIRES_NEW)
    public void createByUserId(
            Integer userId,
            String title,
            String message,
            NotificationType type,
            Integer referenceId) {

        try {
            userRepo.findById(userId)
                    .ifPresentOrElse(u ->
                            save(u, title, message, type, referenceId),
                            () -> log.warn("No user={}", userId));

        }
        catch (Exception ex) {
            log.error(
                    "createByUserId user={}: {}",
                    userId, ex.getMessage());
        }
    }

    @Override
    @Async
    @Transactional(
            propagation =
                    Propagation.REQUIRES_NEW)
    public void createBulkByUserIds(
            List<Integer> userIds,
            String title,
            String message,
            NotificationType type,
            Integer referenceId) {

        if (userIds == null
                || userIds.isEmpty())
            return;
        for (Integer uid : userIds) {
            try {
                userRepo.findById(uid)
                        .ifPresentOrElse(u -> save(u, title, message, type, referenceId),

                                () -> log.warn("No user: {}", uid));
            } catch (Exception ex) {
                log.error(
                        "Bulk userId={}: {}",
                        uid, ex.getMessage());
            }
        }
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
                        notificationId,
                        currentUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ការជូនដំណឹង",
                                notificationId));

        if (Boolean.TRUE.equals(
                n.getIsRead())) {
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
                (req.getUserId() != null
                        ? 1 : 0)
                        + (req.getUserIds() != null
                        && !req.getUserIds()
                        .isEmpty()
                        ? 1 : 0);

        if (count == 0) {
            throw new BusinessException(
                    "សូមជ្រើសរើសគោលដៅអ្នកទទួលការជូនដំណឹង (ជ្រើសរើសជា មន្ត្រីម្នាក់ ឬជាកញ្ចប់មន្ត្រី)។");
        }
        if (count > 1) {
            throw new BusinessException(
                    "លោកអ្នកអាចជ្រើសរើសទម្រង់គោលដៅអ្នកទទួល បានតែមួយប្រភេទប៉ុណ្ណោះ មិនអាចប្រើប្រាស់លាយឡំគ្នាបានឡើយ។");
        }
    }

    private User resolveUser(
            NotificationCreateRequest req) {

        if (req.getUserId() != null) {
            return userRepo
                    .findById(req.getUserId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User",
                                    req.getUserId()));
        }
        throw new BusinessException(
                "ព័ត៌មានអត្តសញ្ញាណអ្នកប្រើប្រាស់ (userId) គឺចាំបាច់ត្រូវតែមាន មិនអាចទទេបានឡើយ។");
    }

    private Notification build(
            User user,
            NotificationCreateRequest req) {
        return Notification.builder()
                .user(user)
                .title(req.getTitle())
                .message(req.getMessage())
                .type(req.getType())
                .referenceId(
                        req.getReferenceId())
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