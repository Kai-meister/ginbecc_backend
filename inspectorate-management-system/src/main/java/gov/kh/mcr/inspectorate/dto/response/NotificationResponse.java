package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NotificationResponse {

    private Integer          notificationId;
    private String           title;
    private String           message;
    private NotificationType type;
    private String           typeLabel;
    private Integer          referenceId;
    private String           navigatePath;
    private Boolean          isRead;
    private LocalDateTime    createdAt;
    private LocalDateTime    readAt;
    private Map<String, Object> referenceData;
}