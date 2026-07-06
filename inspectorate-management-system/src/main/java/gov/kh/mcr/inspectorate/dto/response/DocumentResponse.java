package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DocumentResponse {

    private Integer       documentId;
    private String        documentName;
    private String        documentNumber;
    private String        note;

    private Integer       documentTypeId;
    private String        documentTypeName;

    private Integer       userId;
    private String        userName;
    private String        departmentName;

    private String        statusCode;
    private String        statusLabel;
    private LocalDate     expiryDate;
    private String        fileUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}