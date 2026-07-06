package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnnouncementReportResponse {

    private Integer       no;
    private String        title;
    private String         content;
    @Builder.Default
    private String         createdBy      = "";
    @Builder.Default
    private String         createdByDept  = "";
    @Builder.Default
    private String         priority       = "MEDIUM";
    @Builder.Default
    private String         priorityLabel  = "មធ្យម";
    @Builder.Default
    private String         statusCode     = "";
    @Builder.Default
    private String         statusLabel    = "";
    private LocalDateTime   publishAt;
    private LocalDate       expireAt;
    @Builder.Default
    private Boolean         isExpired     = false;

    @Builder.Default
    private Long            totalRecipients = 0L;
    @Builder.Default
    private Long            readCount       = 0L;
    @Builder.Default
    private Long            unreadCount     = 0L;
    @Builder.Default
    private String          readRate        = "0%";

    private LocalDateTime    createdAt;
}