package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh Token ចាំបាច់")
    private String refreshToken;
}
