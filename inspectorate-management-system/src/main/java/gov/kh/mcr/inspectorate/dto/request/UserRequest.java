package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserRequest {

    @NotNull(message = "តួនាទីចាំបាច់")
    private Integer roleId;

    private Integer officerId;

    @NotBlank(message = "ឈ្មោះខ្មែរចាំបាច់")
    @Size(max = 150)
    private String userNameKh;

    @Size(max = 150)
    private String userNameEn;

    @NotBlank(message = "Email ចាំបាច់")
    @Email(message = "Email មិនត្រឹមត្រូវ")
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    @Pattern(regexp = "^[0-9+\\-\\s]*$",
            message = "លេខទូរស័ព្ទមានតែខ្ទង់")
    private String phone;

    @Size(min = 8, max = 100,
            message = "Password ១-១០០ តួ, យ៉ាងតិច 8")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])"
                    + "(?=.*\\d)(?=.*[@#$%!&*])"
                    + ".{8,}$",
            message = "Password ត្រូវមានអក្សរធំ/តូច/ខ្ទង்/@#$%!")
    private String password;

    @NotBlank(message = "ស្ថានភាពចាំបាច់")
    private String statusCode;
}