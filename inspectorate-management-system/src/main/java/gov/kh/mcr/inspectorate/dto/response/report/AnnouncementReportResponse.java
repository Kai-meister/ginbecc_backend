package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnnouncementReportResponse {

    private Integer       no;
    private String        title;
    private String        createdBy;
    @Builder.Default
    private String  priority       = "";
    @Builder.Default
    private String  priorityLabel  = "";
    private String        statusCode;
    @Builder.Default
    private String  statusLabel    = "";
    @Builder.Default
    private Long    totalRecipients = 0L;
    @Builder.Default
    private Long    readCount       = 0L;
    @Builder.Default
    private Long    unreadCount     = 0L;
    private LocalDateTime publishAt;
    private LocalDateTime createdAt;
}