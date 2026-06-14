package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuditLogReportResponse {

    private Integer       no;
    private String        userNameKh;
    private String        userEmail;
    private String        action;
    private String        actionLabel;
    private String        entityType;
    private Integer       entityId;
    private String        details;
    private String        ipAddress;
    private LocalDateTime createdAt;
}