package gov.kh.mcr.inspectorate.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email ចាំបាច់")
    @Email(message = "Email មិនត្រឹមត្រូវ")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Password ចាំបាច់")
    @Size(min = 1, max = 100)
    private String password;
}
