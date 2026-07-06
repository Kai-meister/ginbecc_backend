package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApprovalReportResponse {

    private Integer       no;
    @Builder.Default
    private String        documentName    = "";
    private String        documentNumber;
    @Builder.Default
    private String        requesterName   = "";
    @Builder.Default
    private String        requesterDept   = "";
    @Builder.Default
    private String        departmentName  = "";

    @Builder.Default
    private String        approvedBy      = "";
    @Builder.Default
    private String        statusCode      = "";
    @Builder.Default
    private String        statusLabel     = "";
    private String        comment;
    private LocalDateTime requestedAt;
    private LocalDateTime decidedAt;
}