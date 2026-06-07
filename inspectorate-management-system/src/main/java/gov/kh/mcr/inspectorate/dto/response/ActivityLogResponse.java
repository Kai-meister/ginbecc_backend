package gov.kh.mcr.inspectorate.dto.response;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ActivityLogResponse {

    private Integer       logId;

    // nullable userId (user deleted case)
    private Integer       userId;
    private String        userNameKh;

    //always has email (even if user deleted)
    private String        userEmail;

    private String        action;
    private String        actionLabel;
    private String        entityType;
    private Integer       entityId;
    private String        details;

    // IP + UserAgent not null
    private String        ipAddress;
    private String        userAgent;

    private LocalDateTime createdAt;
}