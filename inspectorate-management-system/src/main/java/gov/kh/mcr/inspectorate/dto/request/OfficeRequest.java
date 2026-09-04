package gov.kh.mcr.inspectorate.dto.request;


import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfficeRequest {

    @NotNull(message = "សូមជ្រើសរើសនាយកដ្ឋាន")
    private Integer departmentId;

    @NotBlank(message = "សូមបញ្ចូលលេខកូដកិរិយាល័យ")
    @Size(max = 25, message = "លេខកូដកិរិយាល័យមិនអាចលើសពី ២៥ តួអក្សរឡើយ")
    private String officeCode;

    @NotBlank(message = "សូមបញ្ចូលឈ្មោះកិរិយាល័យឱ្យបានត្រឹមត្រូវ")
    @Size(max = 255, message = "ឈ្មោះកិរិយាល័យមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String officeName;

    @NotNull(message = "សូមជ្រើសរើសស្ថានភាពនៃនាយកដ្ឋាន")
    private ActiveStatus status = ActiveStatus.ACTIVE;


}
