package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @NotBlank(message = "សម័យប្រជុំរបស់អ្នកបានផុតកំណត់ ឬខ្វះព័ត៌មានផ្ទៀងផ្ទាត់ (Refresh Token)")
    private String refreshToken;
}