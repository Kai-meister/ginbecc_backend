package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuditLogReportResponse {

    private Integer       no;
    @Builder.Default
    private String        userNameKh   = "SYSTEM";
    @Builder.Default
    private String        userEmail    = "system";
    private String        action;
    @Builder.Default
    private String        actionLabel  = "";
    private String        entityType;
    private Integer       entityId;
    private String        details;
    private String        ipAddress;
    private LocalDateTime createdAt;
}