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


    void createByOfficerId(
            Integer officerId,
            String title,
            String message,
            NotificationType type,
            Integer referenceId);

    void createByUserId(
            Integer userId,
            String title,
            String message,
            NotificationType type,
            Integer referenceId);

    // Broadcast to every ACTIVE user (in-app + FCM), skipping excludeUserId.
    void createForAllActiveUsers(
            String title,
            String message,
            NotificationType type,
            Integer referenceId,
            Integer excludeUserId);

    //  currentUserId for security check
    PageResponse<NotificationResponse>
    getMyNotifications(
            Integer currentUserId,
            Boolean isRead,
            int page, int size);

    // Fix — currentUserId for owner check
    NotificationResponse getById(
            Integer notificationId,
            Integer currentUserId);

    long getUnreadCount(Integer currentUserId);

    void markAsRead(
            Integer notificationId,
            Integer currentUserId);

    int markAllAsRead(Integer currentUserId);
}