package gov.kh.mcr.inspectorate.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusRequest {

    @NotBlank(message = "សូមជ្រើសរើសស្ថានភាព")
    private String statusCode;
}