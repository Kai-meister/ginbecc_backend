package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApprovalResponse {

    private Integer       approvalId;
    private Integer       documentId;
    private String        documentName;

    private String        requestedByName;
    private String        requestedByDept;

    private Integer       departmentId;
    private String        departmentName;

    private String        approvedByName;
    private String        statusCode;
    private String        statusLabel;
    private String        comment;
    private LocalDateTime requestedAt;
    private LocalDateTime decidedAt;
}