package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentTypeRequest {

    @NotBlank(message = "សូមបញ្ចូលលេខកូដប្រភេទឯកសារ")
    @Size(max = 20, message = "លេខកូដប្រភេទឯកសារមិនអាចលើសពី ២០ តួអក្សរឡើយ")
    private String documentTypeCode;

    @NotBlank(message = "សូមបញ្ចូលឈ្មោះប្រភេទឯកសារ")
    @Size(max = 255, message = "ឈ្មោះប្រភេទឯកសារមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String documentTypeName;

    @Size(max = 500, message = "ការពិពណ៌នាអំពីប្រភេទឯកសារមិនអាចលើសពី ៥០០ តួអក្សរឡើយ")
    private String description;

    @NotNull(message = "សូមជ្រើសរើសស្ថានភាពនៃប្រភេទឯកសារ")
    private ActiveStatus status;
}