package gov.kh.mcr.inspectorate.scheduler;
import gov.kh.mcr.inspectorate.entity.Document;
import gov.kh.mcr.inspectorate.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentExpiryScheduler {

    private final DocumentRepository documentRepository;

    @Scheduled(cron = "0 0 9 * * *")
    public void checkExpiringDocuments() {
        LocalDate in30Days = LocalDate.now().plusDays(30);
        List<Document> expiring =
                documentRepository.findExpiring(in30Days);

        log.info("Expiring documents in 30 days: {}",
                expiring.size());

        expiring.forEach(d ->
                log.debug("Expiring: {} | Officer: {} | Date: {}",
                        d.getDocumentName(),
                        d.getUser() != null
                                ? d.getUser().getUserNameKh() : "N/A",
                        d.getExpiryDate()));
    }
}