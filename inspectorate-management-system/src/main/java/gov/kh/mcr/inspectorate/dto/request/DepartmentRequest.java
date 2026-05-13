package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequest {

    @NotBlank(message = "កូដចាំបាច់")
    @Size(max = 20)
    private String departmentCode;

    @NotBlank(message = "ឈ្មោះចាំបាច់")
    @Size(max = 255)
    private String departmentName;

    private String description;

    @NotNull(message = "ស្ថានភាពចាំបាច់")
    private ActiveStatus status;
}
