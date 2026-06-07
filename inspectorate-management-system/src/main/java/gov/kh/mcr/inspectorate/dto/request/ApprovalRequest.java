package gov.kh.mcr.inspectorate.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApprovalRequest {

    @NotNull(message = "ឯកសារចាំបាច់")
    @Positive(message = "documentId > 0")
    private Integer documentId;

    // Optional note from Officer
    @Size(max = 500,
            message = "ghi chú អតិបរមា 500 តួ")
    private String note;
}