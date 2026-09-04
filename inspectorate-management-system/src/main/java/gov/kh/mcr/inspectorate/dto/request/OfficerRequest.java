package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OfficerRequest {

    @NotNull(message = "សូមជ្រើសរើសមុខតំណែង")
    private Integer positionId;

    @NotNull(message = "សូមជ្រើសរើសការិយាល័យ")
    private Integer officeId;

    @NotNull(message = "សូមជ្រើសរើសនាយកដ្ឋាន")
    private Integer departmentId;

    @NotBlank(message = "សូមបញ្ចូលលេខកូដសម្គាល់មន្ត្រី")
    @Size(max = 50, message = "លេខកូដសម្គាល់មន្ត្រីមិនអាចលើសពី ៥០ តួអក្សរឡើយ")
    @Pattern(regexp = "^[A-Za-z0-9\\-_]+$",
            message = "លេខកូដសម្គាល់មន្ត្រីត្រូវប្រើប្រាស់តែអក្សរឡាតាំង លេខ សញ្ញាដក(-) ឬសញ្ញាកាត់(_)")
    private String officerCode;

    @NotBlank(message = "សូមបញ្ចូលនាមត្រកូល និងនាមខ្លួនជាភាសាខ្មែរ")
    @Size(max = 255, message = "ឈ្មោះជាភាសាខ្មែរមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String fullNameKh;

    @Size(max = 255, message = "ឈ្មោះជាភាសាអង់គ្លេសមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String fullNameEn;

    private Gender gender;

    @Past(message = "កាលបរិច្ឆេទកំណើតមិនត្រឹមត្រូវឡើយ (ត្រូវតែជាកាលបរិច្ឆេទក្នុងអតីតកាល)")
    private LocalDate dob;

    @PastOrPresent(message = "កាលបរិច្ឆេទចូលបម្រើការងារត្រូវតែជាថ្ងៃនេះ ឬក្នុងពេលអតីតកាល")
    private LocalDate joinDate;

    private String jobDescription;
    private String educationLevel;
    private String specialization;
    private String salaryGrade;
    private String currentAddress;
    private String birthplace;
    private String livingStatus;

    @Size(max = 20, message = "លេខទូរសព្ទមិនអាចលើសពី ២០ ខ្ទង់ឡើយ")
    @Pattern(regexp = "^[0-9+\\-\\s]*$",
            message = "លេខទូរសព្ទត្រូវមានតែលេខ និងសញ្ញា (+) (-) ឬចន្លោះមិនឃើញ (Space) ប៉ុណ្ណោះ")
    private String phone;

    @Email(message = "ទម្រង់អាសយដ្ឋានអ៊ីមែលមិនត្រឹមត្រូវឡើយ")
    @Size(max = 150, message = "អាសយដ្ឋានអ៊ីមែលមិនអាចលើសពី ១៥០ តួអក្សរឡើយ")
    private String email;

    @NotBlank(message = "សូមជ្រើសរើសស្ថានភាព")
    private String statusCode;
}
