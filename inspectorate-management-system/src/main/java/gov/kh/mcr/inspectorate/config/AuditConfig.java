package gov.kh.mcr.inspectorate.config;


import gov.kh.mcr.inspectorate.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@RequiredArgsConstructor

public class AuditConfig {

    private final SecurityUtils securityUtils;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(
                securityUtils.getCurrentEmail() != null
                        ? securityUtils.getCurrentEmail()
                        : "system");
    }
}