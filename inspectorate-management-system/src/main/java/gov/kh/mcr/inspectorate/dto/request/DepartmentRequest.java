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

    @NotBlank(message = "សូមបញ្ចូលឈ្មោះនាយកដ្ឋានឱ្យបានត្រឹមត្រូវ")
    @Size(max = 255, message = "ឈ្មោះនាយកដ្ឋានមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String departmentName;

    @Size(max = 500, message = "ការពិពណ៌នាអំពីនាយកដ្ឋានមិនអាចលើសពី ៥០០ តួអក្សរឡើយ")
    private String description;

    @NotNull(message = "សូមជ្រើសរើសស្ថានភាពនៃនាយកដ្ឋាន")
    private ActiveStatus status;
}
