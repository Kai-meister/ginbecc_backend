package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    @NotBlank(message = "ពាក្យសម្ងាត់ចាស់ចាំបាច់")
    private String oldPassword;

    @NotBlank(message = "ពាក្យសម្ងាត់ថ្មីចាំបាច់")
    @Size(min = 8, max = 100,
            message = "ពាក្យសម្ងាត់ 8-100 តួ")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])"
                    + "(?=.*\\d)"
                    + "(?=.*[@#$%!&*()_+\\-=])"
                    + ".{8,}$",
            message = "ត្រូវមានអក្សរធំ/តូច/ខ្ទង/និមិត្តសញ្ញា")
    private String newPassword;

    @NotBlank(message = "បញ្ជាក់ Password ចាំបាច់")
    private String confirmPassword;
}