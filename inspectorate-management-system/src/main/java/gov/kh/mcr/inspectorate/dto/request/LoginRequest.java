package gov.kh.mcr.inspectorate.dto.request;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "សូមបញ្ចូលអាសយដ្ឋានអ៊ីមែល")
    @Email(message = "ទម្រង់អាសយដ្ឋានអ៊ីមែលមិនត្រឹមត្រូវឡើយ")
    @Size(max = 150, message = "អាសយដ្ឋានអ៊ីមែលមិនអាចលើសពី ១៥០ តួអក្សរឡើយ")
    private String email;

    @NotBlank(message = "សូមបញ្ចូលពាក្យសម្ងាត់")
    @Size(min = 1, max = 100, message = "ពាក្យសម្ងាត់មិនអាចលើសពី ១០០ តួអក្សរឡើយ")
    private String password;
}
