package gov.kh.mcr.inspectorate.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DecideRequest {
    @NotBlank(message = "សូមជ្រើសរើសស្ថានភាពនៃការសម្រេចចិត្ត")
    @Pattern(
            regexp = "^(APPROVED|REJECTED)$",
            message = "ស្ថានភាពនៃការសម្រេចចិត្តត្រូវតែជា 'APPROVED' (អនុម័ត) ឬ 'REJECTED' (បដិសេធ)")
    private String statusCode;

    @Size(max = 500, message = "មតិយោបល់មិនអាចលើសពី ៥០០ តួអក្សរឡើយ")
    private String comment;

    // Custom validation
    public void validate() {
        if ("REJECTED".equals(statusCode)
                && (comment == null
                || comment.isBlank())) {
            throw new IllegalArgumentException(
                    "សូមបញ្ចូលមតិយោបល់ ឬមូលហេតុនៃការបដិសេធ");
        }
    }
}
