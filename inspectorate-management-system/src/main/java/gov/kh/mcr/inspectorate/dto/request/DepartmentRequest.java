package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequest {

    @NotBlank(message = "សូមបញ្ចូលលេខកូដនាយកដ្ឋាន")
    @Size(max = 20, message = "លេខកូដនាយកដ្ឋានមិនអាចលើសពី ២០ តួអក្សរឡើយ")
    private String departmentCode;

    @NotBlank(message = "សូមបញ្ចូលឈ្មោះនាយកដ្ឋាន")
    @Size(max = 255, message = "ឈ្មោះនាយកដ្ឋានមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String departmentName;

    private String description;

    @NotNull(message = "សូមជ្រើសរើសស្ថានភាពសកម្មភាព")
    private ActiveStatus status;
}
