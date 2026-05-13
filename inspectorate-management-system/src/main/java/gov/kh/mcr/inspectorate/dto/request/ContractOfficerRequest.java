package gov.kh.mcr.inspectorate.dto.request;
import gov.kh.mcr.inspectorate.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractOfficerRequest {

    @NotNull(message = "នាយកដ្ឋានចាំបាច់")
    private Integer departmentId;

    @NotBlank(message = "លេខកូដចាំបាច់")
    @Size(max = 50)
    private String contractOfficerCode;

    @NotBlank(message = "ឈ្មោះខ្មែរចាំបាច់")
    @Size(max = 255)
    private String fullNameKh;

    @Size(max = 255)
    private String fullNameEn;

    private Gender gender;
    private String jobLevel;
    private String jobDescription;

    @NotBlank(message = "ស្ថានភាពចាំបាច់")
    private String statusCode;

    @NotNull(message = "ថ្ងៃចាប់ផ្តើមចាំបាច់")
    private LocalDate startDate;

    @NotNull(message = "ថ្ងៃបញ្ចប់ចាំបាច់")
    private LocalDate endDate;
}
