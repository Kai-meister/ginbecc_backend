package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionRequest {

    @NotBlank(message = "កូដចាំបាច់")
    @Size(max = 20)
    private String positionCode;

    @NotBlank(message = "ឈ្មោះចាំបាច់")
    @Size(max = 255)
    private String positionName;
}
