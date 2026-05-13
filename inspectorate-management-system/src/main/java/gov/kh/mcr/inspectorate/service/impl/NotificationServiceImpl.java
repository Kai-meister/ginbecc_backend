package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.response.NotificationResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.Notification;
import gov.kh.mcr.inspectorate.enums.NotificationType;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.NotificationMapper;
import gov.kh.mcr.inspectorate.repository.NotificationRepository;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import gov.kh.mcr.inspectorate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Async
    public void createNotification(Integer officerId, String title,
                                   String message, String type,
                                   Integer referenceId) {
        userRepository.findByOfficer_OfficerId(officerId)
                .ifPresent(user ->
                        notificationRepository.save(
                                Notification.builder()
                                        .user(user)
                                        .title(title)
                                        .message(message)
                                        .type(NotificationType.valueOf(type))
                                        .referenceId(referenceId)
                                        .isRead(false)
                                        .build()));
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

        Page <Notification>result =
                (isRead != null)
                        ? notificationRepository
                          .findByUser_UserIdAndIsRead(
                                  userId, isRead, pageable)
                        : notificationRepository
                          .findByUser_UserId(
                                  userId, pageable);

        return PageResponse.of(result.map(notificationMapper::toResponse));
    }
    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getById(Integer id) {
        return notificationMapper.toResponse(
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "ការជូនដំណឹង", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Integer userId) {
        return notificationRepository
                .countByUser_UserIdAndIsRead(
                        userId, false);
    }

    @Override
    public void markAsRead(Integer id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ការជូនដំណឹង", id));
        n.setIsRead(true);
        notificationRepository.save(n);
    }

    @Override
    public int markAllAsRead(Integer userId) {
        return notificationRepository.markAllAsRead(userId);
    }
}