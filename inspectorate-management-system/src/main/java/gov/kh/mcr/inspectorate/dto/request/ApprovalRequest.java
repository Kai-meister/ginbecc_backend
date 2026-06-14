package gov.kh.mcr.inspectorate.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApprovalRequest {
    @NotNull(message = "សូមជ្រើសរើសឯកសារ")
    @Positive(message = "លេខសម្គាល់ឯកសារត្រូវមានតម្លៃធំជាង 0")
    private Integer documentId;

    @Size(max = 500,
            message = "កំណត់ចំណាំមិនអាចលើសពី 500 តួអក្សរបានទេ")
    private String note;
}