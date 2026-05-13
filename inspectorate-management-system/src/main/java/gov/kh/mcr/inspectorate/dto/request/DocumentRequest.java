package gov.kh.mcr.inspectorate.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequest {

    @NotNull(message = "មន្ត្រីចាំបាច់")
    private Integer officerId;

    @NotNull(message = "ប្រភេទឯកសារចាំបាច់")
    private Integer documentTypeId;

    private Integer attachmentId;

    @NotBlank(message = "ឈ្មោះឯកសារចាំបាច់")
    @Size(max = 255)
    private String documentName;

    @Size(max = 100)
    private String documentNumber;

    private String    note;

    @NotBlank(message = "ស្ថានភាពចាំបាច់")
    private String    statusCode;

    private LocalDate expiryDate;
}