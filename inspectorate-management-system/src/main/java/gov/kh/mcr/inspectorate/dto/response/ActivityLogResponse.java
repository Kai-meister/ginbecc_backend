package gov.kh.mcr.inspectorate.dto.response;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ActivityLogResponse {

    private Integer       logId;
    private Integer       userId;
    private String        userNameKh;
    private String        userEmail;
    private String        action;
    private String        actionLabel;
    private String        entityType;
    private Integer       entityId;
    private String        details;
    private String        ipAddress;
    private String        userAgent;

    private LocalDateTime createdAt;
}