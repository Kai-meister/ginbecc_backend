package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.enums.NotificationType;

public interface FcmService {

    void sendToUser(Integer userId, String title, String body, NotificationType type, Integer referenceId);
}
