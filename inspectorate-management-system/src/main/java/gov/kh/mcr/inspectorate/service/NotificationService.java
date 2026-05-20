package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.NotificationCreateRequest;
import gov.kh.mcr.inspectorate.dto.response.NotificationResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse create(NotificationCreateRequest request);
    List<NotificationResponse> createBulk(NotificationCreateRequest request);

    void createByOfficerId(
            Integer officerId,
            String title,
            String message,
            String type,
            Integer referenceId,
            String referenceType);

    void createByUserId(
            Integer userId,
            String title,
            String message,
            String type,
            Integer referenceId,
            String referenceType);

    void createNotification(
            Integer officerId,
            String title,
            String message,
            String type,
            Integer referenceId);

    PageResponse<NotificationResponse>
    getMyNotifications(
            Integer userId,
            Boolean isRead,
            int page, int size);

    NotificationResponse getById(Integer id);

    long getUnreadCount(Integer userId);

    void markAsRead(Integer notificationId);

    int markAllAsRead(Integer userId);
}