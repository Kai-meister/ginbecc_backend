package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NotificationReportResponse {

    private Integer       no;
    @Builder.Default
    private String        receiverName  = "";
    @Builder.Default
    private String        receiverEmail = "";
    private String        title;
    private String        message;
    @Builder.Default
    private String         type        = "";
    @Builder.Default
    private String         typeLabel   = "";
    @Builder.Default
    private Boolean         isRead     = false;
    @Builder.Default
    private String          readStatus = "មិនទាន់អាន";
    private LocalDateTime    createdAt;
    private LocalDateTime    readAt;
}