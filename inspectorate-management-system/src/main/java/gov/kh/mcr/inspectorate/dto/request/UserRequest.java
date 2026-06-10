package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.UserType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    // officerId (nullable)
    // null = Admin user (no officer linked)
    private Integer officerId;
    private Integer contractOfficerId;

    @NotNull(message = "Role ចាំបាច់")
    private Integer roleId;

    @NotBlank(message = "ឈ្មោះ KH ចាំបាច់")
    @Size(max = 255)
    private String userNameKh;

    @Size(max = 255)
    private String userNameEn;

    @NotBlank(message = "សូមបញ្ចូលអាសយដ្ឋានអ៊ីមែល")
    @Email(message = "ទម្រង់អាសយដ្ឋានអ៊ីមែលមិនត្រឹមត្រូវឡើយ")
    @Size(max = 150, message = "អាសយដ្ឋានអ៊ីមែលមិនអាចលើសពី ១៥០ តួអក្សរឡើយ")
    private String email;

    @Size(min = 8, max = 100,
            message = "Password ១-១០០ តួ, យ៉ាងតិច 8")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])"
                    + "(?=.*\\d)(?=.*[@#$%!&*])"
                    + ".{8,}$",
            message = "Password ត្រូវមានអក្សរធំ/តូច/សញ្ញា/@#$%!")
    private String password;

    @Size(max = 20)
    private String phone;

    @NotBlank(message = "ស្ថានភាពចាំបាច់")
    private String statusCode;
}