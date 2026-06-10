package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApprovalReportResponse {

    private Integer       no;
    private String        documentName;
    private String        documentNumber;
    private String        requestedBy;
    private String        requestedByDept;
    private String        approvedBy;
    private String        statusCode;
    private String        statusLabel;
    private String        comment;
    private LocalDateTime requestedAt;
    private LocalDateTime decidedAt;
}