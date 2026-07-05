package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest {

    @NotNull(message = "សូមជ្រើសរើសឯកសារដែលត្រូវអនុម័ត")
    @Positive(message = "លេខសម្គាល់ឯកសារត្រូវតែជាលេខវិជ្ជមាន")
    private Integer documentId;

    @Size(max = 500, message = "កំណត់ចំណាំមិនអាចលើសពី ៥០០ តួអក្សរបានឡើយ")
    private String note;
}