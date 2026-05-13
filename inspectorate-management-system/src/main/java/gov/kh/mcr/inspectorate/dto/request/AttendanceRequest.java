package gov.kh.mcr.inspectorate.dto.request;


import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRequest {

    @NotNull(message = "ស្ថានភាពវត្តមានចាំបាច់")
    private AttendanceStatus attendanceStatus;

    private String note;
}
