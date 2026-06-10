package gov.kh.mcr.inspectorate.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequest {

    @NotNull(message = "សូមជ្រើសរើសមន្ត្រីទទួលខុសត្រូវ")
    private Integer officerId;

    @NotNull(message = "សូមជ្រើសរើសប្រភេទឯកសារ")
    private Integer documentTypeId;
    @NotBlank(message = "ឈ្មោះឯកសារចាំបាច់")
    @Size(max = 255)
    private String documentName;

    @Size(max = 100, message = "លេខឯកសារមិនអាចលើសពី ១០០ តួអក្សរឡើយ")
    private String documentNumber;

    private String note;

//    @NotBlank(message = "ស្ថានភាពចាំបាច់")
//    private String    statusCode;

    private LocalDate expiryDate;
}