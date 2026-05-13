package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OfficerRequest {

    @NotNull(message = "តំណែងចាំបាច់")
    private Integer positionId;

    @NotNull(message = "នាយកដ្ឋានចាំបាច់")
    private Integer departmentId;

    @NotBlank(message = "លេខកូដចាំបាច់")
    @Size(max = 50,
            message = "លេខកូដអតិបរមា 50 តួ")
    @Pattern(regexp = "^[A-Za-z0-9\\-_]+$",
            message = "លេខកូដអាក្សរឡាតាំង/ខ្ទង់/dash")
    private String officerCode;

    @NotBlank(message = "ឈ្មោះខ្មែរចាំបាច់")
    @Size(max = 255)
    private String fullNameKh;

    @Size(max = 255)
    private String fullNameEn;

    private Gender gender;

    @Past(message = "ថ្ងៃកំណើតត្រូវជាអតីតកាល")
    private LocalDate dob;

    @PastOrPresent(
            message = "ថ្ងៃចូលការងារត្រូវតែបន្ទាប់ ឬថ្ងៃនេះ")
    private LocalDate joinDate;

    private String jobDescription;
    private String educationLevel;
    private String specialization;
    private String salaryGrade;
    private String currentAddress;
    private String birthplace;
    private String livingStatus;

    @Size(max = 20)
    @Pattern(regexp = "^[0-9+\\-\\s]*$",
            message = "លេខទូរស័ព្ទមានតែខ្ទង់")
    private String phone;

    @Email(message = "Email មិនត្រឹមត្រូវ")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "ស្ថានភាពចាំបាច់")
    private String statusCode;
}