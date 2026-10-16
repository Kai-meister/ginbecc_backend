package gov.kh.mcr.inspectorate.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Adds {@code users.must_change_password} before Hibernate's schema update.
 * {@code ddl-auto: update} emits {@code ADD COLUMN ... NOT NULL} with no
 * default, which PostgreSQL rejects once {@code users} already has rows.
 */
@Slf4j
@Component
public class MustChangePasswordColumnPatch implements BeanPostProcessor {

    private final AtomicBoolean applied = new AtomicBoolean(false);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof DataSource dataSource && applied.compareAndSet(false, true)) {
            apply(dataSource);
        }
        return bean;
    }

    private void apply(DataSource dataSource) {
        String sql = """
                ALTER TABLE users
                ADD COLUMN IF NOT EXISTS must_change_password boolean NOT NULL DEFAULT false
                """;
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
            log.info("Schema patch OK: users.must_change_password");
        } catch (SQLException e) {
            if ("42P01".equals(e.getSQLState())) {
                log.info("users table does not exist yet; Hibernate will create must_change_password");
                return;
            }
            throw new IllegalStateException(
                    "Could not add users.must_change_password", e);
        }
    }
}
