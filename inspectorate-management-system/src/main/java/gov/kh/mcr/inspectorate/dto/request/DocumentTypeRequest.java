package gov.kh.mcr.inspectorate.dto.request;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTypeRequest {

    @NotBlank(message = "កូដចាំបាច់")
    @Size(max = 20)
    private String documentTypeCode;

    @NotBlank(message = "ឈ្មោះចាំបាច់")
    @Size(max = 255)
    private String documentTypeName;

    private String description;
    private ActiveStatus status;
}
