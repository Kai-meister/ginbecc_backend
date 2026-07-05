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

    @NotNull(message = "សូមជ្រើសរើសនាយកដ្ឋានសាមីខ្លួន")
    @Positive(message = "លេខសម្គាល់នាយកដ្ឋានត្រូវតែជាលេខវិជ្ជមាន")
    private Integer departmentId;

    @NotBlank(message = "សូមបញ្ចូលលេខកូដមន្ត្រីកិច្ចសន្យា")
    @Size(max = 50, message = "លេខកូដមន្ត្រីកិច្ចសន្យាមិនអាចលើសពី ៥០ តួអក្សរឡើយ")
    private String contractOfficerCode;

    @NotBlank(message = "សូមបញ្ចូលនាមត្រកូល និងនាមខ្លួនជាភាសាខ្មែរ")
    @Size(max = 255, message = "ឈ្មោះជាភាសាខ្មែរមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String fullNameKh;

    @Size(max = 255, message = "ឈ្មោះជាភាសាអង់គ្លេសមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String fullNameEn;

    private Gender gender;

    private LocalDate dob;

    @Size(max = 20, message = "លេខទូរសព្ទមិនអាចលើសពី ២០ ខ្ទង់ឡើយ")
    private String phone;

    @Email(message = "ទម្រង់អ៊ីមែលមិនត្រឹមត្រូវឡើយ")
    @Size(max = 150, message = "អ៊ីមែលមិនអាចលើសពី ១៥០ តួអក្សរឡើយ")
    private String email;

    @Size(max = 50, message = "កម្រិតការងារ ឬមុខតំណែងមិនអាចលើសពី ៥០ តួអក្សរឡើយ")
    private String jobLevel;
    @Size(max = 250, message = "ការពិពណ៌នាពីការងារមិនអាចលើសពី ២៥០ តួអក្សរឡើយ")
    private String jobDescription;

    @NotNull(message = "សូមជ្រើសរើសកាលបរិច្ឆេទចាប់ផ្តើមចុះកិច្ចសន្យា")
    private LocalDate startDate;

    @NotNull(message = "សូមជ្រើសរើសកាលបរិច្ឆេទបញ្ចប់កិច្ចសន្យា")
    private LocalDate endDate;

    private String note;

    @Size(max = 50, message = "កូដគណនេយ្យមិនអាចលើសពី ៥០ តួអក្សរឡើយ")
    private String accountingCode;

    @NotBlank(message = "សូមជ្រើសរើសស្ថានភាពនៃកិច្ចសន្យា")
    private String statusCode;
}