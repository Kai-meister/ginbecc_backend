package gov.kh.mcr.inspectorate.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * AttachmentRefType.OFFICER was renamed to OFFICER_PROFILE, but the enum
 * name is stored as a string in attachments.reference_type, so rows written
 * before the rename break Hibernate's Enum.valueOf on read.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class LegacyAttachmentRefTypeMigration
        implements CommandLineRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(String... args) {
        int updated = jdbc.update(
                "UPDATE attachments "
                        + "SET reference_type = 'OFFICER_PROFILE' "
                        + "WHERE reference_type = 'OFFICER'");

        if (updated > 0) {
            log.info("Migrated {} attachment(s) "
                    + "OFFICER -> OFFICER_PROFILE", updated);
        }
    }
}
