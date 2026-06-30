package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequest {

    @NotNull(message = "សូមជ្រើសរើសប្រភេទឯកសារ")
    @Positive(message = "លេខសម្គាល់ប្រភេទឯកសារត្រូវតែជាលេខវិជ្ជមាន")
    private Integer documentTypeId;

    @NotBlank(message = "សូមបញ្ចូលឈ្មោះឯកសារ")
    @Size(max = 255, message = "ឈ្មោះឯកសារមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String documentName;

    @Size(max = 100, message = "លេខយោងឯកសារមិនអាចលើសពី ១០០ តួអក្សរឡើយ")
    private String documentNumber;

    @Size(max = 500, message = "កំណត់ចំណាំមិនអាចលើសពី ៥០០ តួអក្សរឡើយ")
    private String note;

    private LocalDate expiryDate;
}