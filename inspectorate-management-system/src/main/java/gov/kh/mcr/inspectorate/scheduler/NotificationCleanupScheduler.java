package gov.kh.mcr.inspectorate.scheduler;
import gov.kh.mcr.inspectorate.repository.ActivityLogRepository;
import gov.kh.mcr.inspectorate.repository.NotificationRepository;
import gov.kh.mcr.inspectorate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private final NotificationRepository
            notifRepository;
    private final ActivityLogRepository
            logRepository;
    private final UserRepository
            userRepository;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupOldNotifications() {
        LocalDateTime cutoff = LocalDateTime.now()
                .minusDays(30);

        // Delete read notifications > 30 days
        userRepository.findAll()
                .forEach(user -> {
                    int deleted =
                            notifRepository
                                    .deleteOldByUser(
                                            user.getUserId(),
                                            cutoff);
                    if (deleted > 0) {
                        log.debug(
                                "Deleted {} old notifs"
                                        + " for user: {}",
                                deleted,
                                user.getEmail());
                    }
                });

        log.info(
                "Notification cleanup done"
                        + " (before {})", cutoff);
    }

    // Cleanup audit logs > 1 year
    // Run: 03:00 AM on 1st of month
    @Scheduled(cron = "0 0 3 1 * *")
    @Transactional
    public void cleanupOldAuditLogs() {
        LocalDateTime cutoff = LocalDateTime.now()
                .minusYears(1);

        int deleted =
                logRepository.deleteOlderThan(cutoff);

        log.info(
                "Audit log cleanup: deleted {} rows"
                        + " (before {})",
                deleted, cutoff);
    }
}
