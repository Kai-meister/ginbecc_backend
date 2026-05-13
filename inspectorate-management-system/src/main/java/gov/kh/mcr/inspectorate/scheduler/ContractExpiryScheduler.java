package gov.kh.mcr.inspectorate.scheduler;

import gov.kh.mcr.inspectorate.entity.ContractOfficer;
import gov.kh.mcr.inspectorate.repository.ContractOfficerRepository;
import gov.kh.mcr.inspectorate.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractExpiryScheduler {

    private final ContractOfficerRepository contractRepo;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void notifyExpiringContracts() {
        LocalDate in30Days = LocalDate.now().plusDays(30);
        List<ContractOfficer> expiring =
                contractRepo.findExpiring(in30Days);

        expiring.forEach(c -> {
            if (c.getDepartment() != null) {
                log.info(
                        "Contract expiring: {} on {}",
                        c.getFullNameKh(), c.getEndDate());
            }
        });

        log.info("Contract expiry check: {} found",
                expiring.size());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateExpiredContracts() {
        log.info("Daily contract status update");
    }
}
