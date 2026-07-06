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
//    private final UserRepository
//            userRepository;

    //  Cleanup read > 30 days
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupOldNotifications() {

        LocalDateTime cutoff =
                LocalDateTime.now().minusDays(30);

        int deleted =
                notifRepository.deleteOldRead(cutoff);

        log.info(
                "Notification cleanup:"
                        + " deleted {} (before {})",
                deleted, cutoff);
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
