package gov.kh.mcr.inspectorate.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * ddl-auto: update only ever adds columns — it never drops old ones or
 * relaxes their constraints. These leftovers from earlier schema versions
 * reject inserts written by the current entities:
 *
 * - meeting_attendees.officer_id NOT NULL   (attendees are user-based now)
 * - approvals.requested_by_officer_id NOT NULL (no longer mapped)
 * - meeting_rooms_status_check              (predates the IN_USE status)
 * - officers_gender_check                   (predates the MONK gender)
 */
@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class LegacySchemaCleanupMigration
        implements CommandLineRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(String... args) {
        dropNotNull("meeting_attendees", "officer_id");
        dropNotNull("approvals", "requested_by_officer_id");
        dropConstraint("meeting_rooms", "meeting_rooms_status_check");
        dropConstraint("officers", "officers_gender_check");
    }

    private void dropNotNull(String table, String column) {
        run("column " + table + "." + column, """
            DO $$ BEGIN
                IF EXISTS (
                    SELECT 1 FROM information_schema.columns
                    WHERE table_name = '%s'
                    AND   column_name = '%s'
                    AND   is_nullable = 'NO')
                THEN
                    ALTER TABLE %s ALTER COLUMN %s DROP NOT NULL;
                    RAISE NOTICE 'dropped NOT NULL';
                END IF;
            END $$;
            """.formatted(table, column, table, column));
    }

    private void dropConstraint(String table, String constraint) {
        run("constraint " + constraint,
                "ALTER TABLE " + table
                        + " DROP CONSTRAINT IF EXISTS " + constraint);
    }

    private void run(String what, String sql) {
        try {
            jdbc.execute(sql);
            log.info("Legacy schema cleanup OK: {}", what);
        } catch (Exception e) {
            log.warn("Legacy schema cleanup skipped ({}): {}",
                    what, e.getMessage());
        }
    }
}
