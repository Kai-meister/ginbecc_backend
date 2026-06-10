package gov.kh.mcr.inspectorate.dto.response;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApprovalResponse {

    private Integer       approvalId;

    // Document info
    private Integer       documentId;
    private String        documentName;
    private String        documentNumber;

    // Officer who requested
    private Integer       requestedByOfficerId;
    private String        requestedByName;

    // User who decided
    private Integer       approvedByUserId;
    private String        approvedByName;

    // Status
    private String        statusCode;
    private String        statusLabel;

    // Decision detail
    private String        comment;

    // Timestamps
    private LocalDateTime requestedAt;
    private LocalDateTime decidedAt;
    private LocalDateTime createdAt;
}