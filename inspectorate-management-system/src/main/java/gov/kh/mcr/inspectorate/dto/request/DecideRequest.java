package gov.kh.mcr.inspectorate.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DecideRequest {

    @NotBlank(message = "statusCode ចាំបាច់")
    @Pattern(
            regexp = "^(APPROVED|REJECTED)$",
            message = "statusCode: APPROVED ឬ REJECTED")
    private String statusCode;

    // Required when REJECTED
    private String comment;

    // Custom validation
    public void validate() {
        if ("REJECTED".equals(statusCode)
                && (comment == null
                || comment.isBlank())) {
            throw new IllegalArgumentException(
                    "ការបដិសេធត្រូវការ comment");
        }
    }
}
