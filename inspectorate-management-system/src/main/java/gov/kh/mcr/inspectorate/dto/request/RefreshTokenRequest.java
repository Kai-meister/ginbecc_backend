package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @NotBlank(message = "សូមផ្តល់ជូនថូខិនសារជាថ្មី (Refresh Token)")
    private String refreshToken;
}
