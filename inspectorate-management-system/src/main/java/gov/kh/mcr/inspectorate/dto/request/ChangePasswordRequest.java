package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChangePasswordRequest {
    @NotBlank(message = "សូមបញ្ចូលពាក្យសម្ងាត់ចាស់")
    private String oldPassword;

    @NotBlank(message = "សូមបញ្ចូលពាក្យសម្ងាត់ថ្មី")
    @Size(min = 8, max = 100,
            message = "ពាក្យសម្ងាត់ថ្មីត្រូវមានប្រវែងពី ៨ ដល់ ១០០ តួអក្សរ")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])"
                    + "(?=.*\\d)"
                    + "(?=.*[@#$%!&*()_+\\-=])"
                    + ".{8,}$",
            message = "ពាក្យសម្ងាត់ត្រូវមានលាយអក្សរធំ អក្សរតូច លេខ និងនិមិត្តសញ្ញាពិសេស")
    private String newPassword;

    @NotBlank(message = "សូមបញ្ចូលពាក្យសម្ងាត់ម្តងទៀតដើម្បីផ្ទៀងផ្ទាត់")
    private String confirmPassword;
}