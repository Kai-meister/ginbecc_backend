package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.response.NotificationResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;

public interface NotificationService {

    void createNotification(
            Integer officerId, String title,
            String message, String type,
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
