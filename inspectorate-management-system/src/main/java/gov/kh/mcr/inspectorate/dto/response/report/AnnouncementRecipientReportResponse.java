package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnnouncementRecipientReportResponse {
    private Integer       no;
    @Builder.Default
    private String        announcementTitle = "";
    @Builder.Default
    private String        receiverName      = "";
    @Builder.Default
    private String        receiverEmail     = "";
    @Builder.Default
    private String        departmentName    = "";
    @Builder.Default
    private Boolean        isRead          = false;
    @Builder.Default
    private String          readStatus      = "មិនទាន់អាន";
    private LocalDateTime    readAt;
    private LocalDateTime    createdAt;
}