package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionRequest {

    @NotNull(message = "សូមជ្រើសរើសនាយកដ្ឋាន")
    @Positive(message = "អត្តសញ្ញាណនាយកដ្ឋានមិនត្រឹមត្រូវឡើយ")
    private Integer departmentId;

    @NotBlank(message = "សូមបញ្ចូលលេខកូដមុខតំណែង")
    @Size(max = 50, message = "លេខកូដមុខតំណែងមិនអាចលើសពី ៥០ តួអក្សរឡើយ")
    private String positionCode;

    @NotBlank(message = "សូមបញ្ចូលឈ្មោះមុខតំណែងជាភាសាខ្មែរ")
    @Size(max = 255, message = "ឈ្មោះមុខតំណែងជាភាសាខ្មែរមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String positionName;

    @Size(max = 255, message = "ឈ្មោះមុខតំណែងជាភាសាអង់គ្លេសមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String positionNameEn;

    @Size(max = 1000, message = "ការពិពណ៌នាមិនអាចលើសពី ១០០០ តួអក្សរឡើយ")
    private String description;
}