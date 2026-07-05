package gov.kh.mcr.inspectorate.service.impl;

import com.google.firebase.messaging.*;
import gov.kh.mcr.inspectorate.entity.Notification;
import gov.kh.mcr.inspectorate.entity.UserDevice;
import gov.kh.mcr.inspectorate.enums.NotificationType;
import gov.kh.mcr.inspectorate.repository.UserDeviceRepository;
import gov.kh.mcr.inspectorate.service.FcmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    private final UserDeviceRepository userDeviceRepository;
    private final ObjectProvider<FirebaseMessaging> firebaseMessagingProvider;

    @Async
    @Override
    public void sendToUser(Integer userId, String title, String body, NotificationType type, Integer referenceId) {
        FirebaseMessaging firebaseMessaging = firebaseMessagingProvider.getIfAvailable();
        if (firebaseMessaging == null) {
            log.debug("Firebase disabled — skipping push for userId={}", userId);
            return;
        }

        List<UserDevice> devices = userDeviceRepository.findByUserUserId(userId);
        if (devices.isEmpty()) {
            log.debug("No devices registered for userId={}", userId);
            return;
        }

        List<String> tokens = devices.stream()
                .map(UserDevice::getToken)
                .toList();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("type", type.getCode())
                .putData("path", type.buildPath(referenceId))
                .putData("referenceId", referenceId != null ? referenceId.toString() : "")
                .addAllTokens(tokens)
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            pruneDeadTokens(response, tokens);
        } catch (FirebaseMessagingException e) {
            log.warn("FCM multicast failed for userId={}: {}", userId, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected FCM error for userId={}: {}", userId, e.getMessage());
        }
    }

    private void pruneDeadTokens(BatchResponse response, List<String> tokens) {
        List<String> deadTokens = new ArrayList<>();
        List<SendResponse> responses = response.getResponses();

        for (int i = 0; i < responses.size(); i++) {
            SendResponse sr = responses.get(i);
            if (!sr.isSuccessful()) {
                MessagingErrorCode code = sr.getException().getMessagingErrorCode();
                if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT) {
                    deadTokens.add(tokens.get(i));
                }
            }
        }

        if (!deadTokens.isEmpty()) {
            log.warn("Pruning {} dead FCM token(s)", deadTokens.size());
            userDeviceRepository.deleteByTokenIn(deadTokens);
        }
    }
}