package gov.kh.mcr.inspectorate.dto.response;



import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalResponse {

    private Integer approvalId;
    private Integer documentId;
    private String documentName;
    private String requestedByName;
    private LocalDateTime requestedAt;
    private String approvedByName;
    private String statusCode;
    private String statusLabel;
    private String comment;
    private LocalDateTime approvedAt;
}