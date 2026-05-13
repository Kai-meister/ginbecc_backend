package gov.kh.mcr.inspectorate.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest {

    @NotNull(message = "ឯកសារចាំបាច់")
    private Integer documentId;
}