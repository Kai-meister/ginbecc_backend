package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request
        .NotificationCreateRequest;
import gov.kh.mcr.inspectorate.dto.response
        .NotificationResponse;
import gov.kh.mcr.inspectorate.dto.response
        .PageResponse;
import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import java.util.List;

public interface NotificationService {

    NotificationResponse create(
            NotificationCreateRequest request);

    List<NotificationResponse> createBulk(
            NotificationCreateRequest request);

    void createByUserId(
            Integer userId,
            String title,
            String message,
            NotificationType type,
            Integer referenceId);

    void createBulkByUserIds(
            List<Integer> userIds,
            String title,
            String message,
            NotificationType type,
            Integer referenceId);


    PageResponse<NotificationResponse>
    getMyNotifications(
            Integer currentUserId,
            Boolean isRead,
            int page, int size);

    NotificationResponse getById(
            Integer notificationId,
            Integer currentUserId);

    long getUnreadCount(Integer currentUserId);


    void markAsRead(
            Integer notificationId,
            Integer currentUserId);

    PageResponse<NotificationResponse>
    getMyNotificationsByType(
            Integer currentUserId,
            NotificationType type,
            Boolean isRead,
            int page, int size);

    int markAllAsRead(Integer currentUserId);
}