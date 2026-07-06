package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceUpdateRequest {

    @NotNull(message = "ស្ថានភាពវត្តមានចាំបាច់ត្រូវតែជ្រើសរើស")
    private AttendanceStatus attendanceStatus;

    @Size(max = 500, message = "កំណត់ចំណាំមិនអាចលើសពី ៥០០ តួអក្សរបានឡើយ")
    private String note;
}