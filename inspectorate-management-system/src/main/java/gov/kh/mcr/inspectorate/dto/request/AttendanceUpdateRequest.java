package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttendanceUpdateRequest {

    @NotNull(message = "ស្ថានភាពចាំបាច់")
    private AttendanceStatus attendanceStatus;

    private String note;
}