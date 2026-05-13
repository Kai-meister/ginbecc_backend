package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTypeResponse {

    private Integer documentTypeId;
    private String documentTypeCode;
    private String documentTypeName;
    private String description;
    private ActiveStatus status;
    private LocalDateTime createdAt;
}