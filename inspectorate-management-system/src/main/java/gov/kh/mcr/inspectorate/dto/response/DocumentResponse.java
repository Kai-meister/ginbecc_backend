package gov.kh.mcr.inspectorate.dto.response;



import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {

    private Integer documentId;
    private String officerName;
    private String documentTypeName;
    private String  documentName;
    private String documentNumber;
    private String note;
    private String statusCode;
    private String statusLabel;
    private LocalDate expiryDate;
    private String fileUrl;
    private LocalDateTime createdAt;
}
