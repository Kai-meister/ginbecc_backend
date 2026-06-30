package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.UserType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotNull(message = "សូមជ្រើសរើសប្រភេទអ្នកប្រើប្រាស់")
    private UserType userType;

    private Integer officerId;
    private Integer contractOfficerId;

    @NotNull(message = "សូមជ្រើសរើសតួនាទីប្រព័ន្ធ")
    private Integer roleId;

    @NotBlank(message = "សូមបញ្ចូលនាមត្រកូល និងនាមខ្លួនជាភាសាខ្មែរ")
    @Size(max = 255, message = "ឈ្មោះជាភាសាខ្មែរមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String userNameKh;

    @Size(max = 255, message = "ឈ្មោះជាភាសាអង់គ្លេសមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String userNameEn;

    @NotBlank(message = "សូមបញ្ចូលអាសយដ្ឋានអ៊ីមែល")
    @Email(message = "ទម្រង់អាសយដ្ឋានអ៊ីមែលមិនត្រឹមត្រូវឡើយ")
    @Size(max = 150, message = "អាសយដ្ឋានអ៊ីមែលមិនអាចលើសពី ១៥០ តួអក្សរឡើយ")
    private String email;

    @NotBlank(message = "សូមបញ្ចូលពាក្យសម្ងាត់")
    @Size(min = 8, max = 100, message = "ពាក្យសម្ងាត់ត្រូវមានប្រវែងយ៉ាងហោចណាស់ ៨ តួអក្សរ")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%!&*]).{8,}$",
            message = "ពាក្យសម្ងាត់ត្រូវមានលាយអក្សរធំ អក្សរតូច លេខ និងនិមិត្តសញ្ញាពិសេស (ឧទាហរណ៍៖ @, #, $, %)")
    private String password;

    @Size(max = 20, message = "លេខទូរសព្ទមិនអាចលើសពី ២០ ខ្ទង់ឡើយ")
    @Pattern(regexp = "^[0-9+\\s-]*$", message = "ទម្រង់លេខទូរសព្ទមិនត្រឹមត្រូវឡើយ")
    private String phone;

    @NotBlank(message = "សូមជ្រើសរើសស្ថានភាពគណនី")
    private String statusCode;
}