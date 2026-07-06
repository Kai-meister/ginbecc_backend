package gov.kh.mcr.inspectorate.scheduler;

import gov.kh.mcr.inspectorate.repository
        .AnnouncementRepository;
import gov.kh.mcr.inspectorate.service
        .NotificationService;
import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation
        .Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation
        .Transactional;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementExpireScheduler {

    private final AnnouncementRepository
            announcementRepo;
    private final NotificationService
            notificationService;

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void checkExpiredAnnouncements() {

        LocalDate in3Days =
                LocalDate.now().plusDays(3);

        var expiringSoon = announcementRepo
                .findExpiringSoon(in3Days);

        expiringSoon.forEach(a -> {

            long daysLeft =
                    java.time.temporal.ChronoUnit
                            .DAYS.between(
                                    LocalDate.now(),
                                    a.getExpireAt());

            if (a.getCreatedBy() != null) {
                notificationService
                        .createByUserId(
                                a.getCreatedBy()
                                        .getUserId(),
                                "ប្រកាសជិតផុតកំណត់ ",
                                "ប្រកាស \""
                                        + a.getTitle()
                                        + "\" ផុត"
                                        + (daysLeft == 0
                                        ? "ថ្ងៃនេះ"
                                        : "ក្នុង "
                                          + daysLeft
                                          + " ថ្ងៃ"),
                                NotificationType
                                        .ANNOUNCEMENT,
                                a.getAnnouncementId());
            }

            log.info(
                    "Expiring: ann={} in {}d",
                    a.getAnnouncementId(),
                    daysLeft);
        });

        log.info("Expire check done: {} soon",
                expiringSoon.size());
    }
}