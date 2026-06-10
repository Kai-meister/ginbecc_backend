package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DocumentReportResponse {

    private Integer       no;
    private String        documentNumber;
    private String        documentName;
    @Builder.Default
    private String  documentTypeName = "";
    @Builder.Default
    private String  officerName    = "";
    private String        departmentName;
    private LocalDate     expiryDate;
    @Builder.Default
    private Boolean isExpired      = false;
    private String        statusCode;
    @Builder.Default
    private String  statusLabel    = "";
    private String        uploadedBy;
    private LocalDateTime createdAt;
}