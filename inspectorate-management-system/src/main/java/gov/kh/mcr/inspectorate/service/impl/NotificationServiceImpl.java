package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.NotificationCreateRequest;
import gov.kh.mcr.inspectorate.dto.response.NotificationResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.Notification;
import gov.kh.mcr.inspectorate.entity.User;
import gov.kh.mcr.inspectorate.enums.NotificationType;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.NotificationMapper;
import gov.kh.mcr.inspectorate.repository.NotificationRepository;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import gov.kh.mcr.inspectorate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl
        implements NotificationService {
    private final NotificationRepository notifRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notifMapper;

    @Override
    public NotificationResponse create(
            NotificationCreateRequest request) {

        // Validate — ត្រូវ specify target មួយ
        validateTarget(request);

        // Resolve User
        User user = resolveUser(request);

        Notification saved = notifRepository.save(
                buildNotification(user, request));

        log.info("Notification created: [{}] → {}",
                request.getType(),
                user.getEmail());

        return notifMapper.toResponse(saved);
    }

    @Override
    public List<NotificationResponse> createBulk(
            NotificationCreateRequest request) {

        if (request.getOfficerIds() == null
                || request.getOfficerIds().isEmpty()) {
            throw new BusinessException(
                    "officerIds ចាំបាច់សម្រាប់ Bulk");
        }

        List<NotificationResponse> results =
                new ArrayList<>();

        request.getOfficerIds().forEach(officerId -> {
            userRepository
                    .findByOfficer_OfficerId(officerId)
                    .ifPresentOrElse(
                            user -> {
                                Notification saved =
                                        notifRepository.save(
                                                buildNotification(
                                                        user, request));
                                results.add(
                                        notifMapper.toResponse(
                                                saved));
                            },
                            () -> log.warn(
                                    "No user for officer: {}",
                                    officerId));
        });

        log.info(
                "Bulk notifications created:"
                        + " {}/{} success",
                results.size(),
                request.getOfficerIds().size());

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
            String type,
            Integer referenceId,
            String referenceType) {

        userRepository
                .findByOfficer_OfficerId(officerId)
                .ifPresentOrElse(
                        user -> save(
                                user.getUserId(), title,
                                message, type,
                                referenceId, referenceType),
                        () -> log.warn(
                                "No user for officer: {}",
                                officerId));
    }

    @Override
    @Async
    @Transactional(
            propagation = Propagation.REQUIRES_NEW)
    public void createByUserId(
            Integer userId,
            String title,
            String message,
            String type,
            Integer referenceId,
            String referenceType) {

        save(userId, title, message,
                type, referenceId, referenceType);
    }

    @Override
    @Async
    @Transactional(
            propagation = Propagation.REQUIRES_NEW)
    public void createNotification(
            Integer officerId,
            String title,
            String message,
            String type,
            Integer referenceId) {

        createByOfficerId(
                officerId, title, message,
                type, referenceId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse>
    getMyNotifications(
            Integer userId,
            Boolean isRead,
            int page, int size) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        Page<Notification> result =
                isRead != null
                        ? notifRepository
                          .findByUser_UserIdAndIsRead(
                                  userId, isRead, pageable)
                        : notifRepository
                          .findByUser_UserId(
                                  userId, pageable);

        return PageResponse.of(
                result.map(notifMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getById(Integer id) {
        return notifMapper.toResponse(
                notifRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "ការជូនដំណឹង", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Integer userId) {
        return notifRepository
                .countByUser_UserIdAndIsRead(
                        userId, false);
    }

    @Override
    public void markAsRead(Integer id) {
        Notification n =
                notifRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "ការជូនដំណឹង", id));

        if (Boolean.TRUE.equals(n.getIsRead())) {
            return;
        }

        n.setIsRead(true);
        notifRepository.save(n);
    }

    @Override
    public int markAllAsRead(Integer userId) {
        return notifRepository
                .markAllAsRead(userId);
    }

    private void validateTarget(
            NotificationCreateRequest request) {

        boolean hasOfficer =
                request.getOfficerId() != null;
        boolean hasUser =
                request.getUserId() != null;
        boolean hasBulk =
                request.getOfficerIds() != null
                        && !request.getOfficerIds().isEmpty();

        long count = (hasOfficer ? 1 : 0)
                + (hasUser   ? 1 : 0)
                + (hasBulk   ? 1 : 0);

        if (count == 0) {
            throw new BusinessException(
                    "ត្រូវ specify target:"
                            + " officerId / userId"
                            + " / officerIds (bulk)");
        }

        if (count > 1) {
            throw new BusinessException(
                    "specify target តែ 1"
                            + " officerId / userId"
                            + " / officerIds");
        }
    }

    private User resolveUser(
            NotificationCreateRequest request) {

        if (request.getUserId() != null) {
            return userRepository
                    .findById(request.getUserId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User",
                                    request.getUserId()));
        }

        if (request.getOfficerId() != null) {
            return userRepository
                    .findByOfficer_OfficerId(
                            request.getOfficerId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User សម្រាប់ Officer",
                                    request.getOfficerId()));
        }

        throw new BusinessException(
                "មិនអាច resolve User");
    }

    private Notification buildNotification(
            User user,
            NotificationCreateRequest request) {

        return Notification.builder()
                .user(user)
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .referenceId(request.getReferenceId())
                .referenceType(
                        request.getReferenceType() != null
                                ? request.getReferenceType()
                                : request.getType().name())
                .isRead(false)
                .build();
    }

    private void save(
            Integer userId,
            String title,
            String message,
            String type,
            Integer referenceId,
            String referenceType) {

        try {
            userRepository.findById(userId)
                    .ifPresentOrElse(
                            user -> notifRepository.save(
                                    Notification.builder()
                                            .user(user)
                                            .title(title)
                                            .message(message)
                                            .type(NotificationType
                                                    .valueOf(type))
                                            .referenceId(referenceId)
                                            .referenceType(
                                                    referenceType)
                                            .isRead(false)
                                            .build()),
                            () -> log.warn(
                                    "User not found: {}",
                                    userId));
        } catch (Exception ex) {
            log.error(
                    "Notification save failed: {}",
                    ex.getMessage());
        }
    }
}
