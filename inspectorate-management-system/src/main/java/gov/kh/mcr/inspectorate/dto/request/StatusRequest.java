package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusRequest {

    @NotBlank(message = "សូមបញ្ចូល ឬជ្រើសរើសកូដស្ថានភាព")
    @Size(max = 50, message = "កូដស្ថានភាពមិនអាចលើសពី ៥០ តួអក្សរឡើយ")
    private String statusCode;
}