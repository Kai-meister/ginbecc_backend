package gov.kh.mcr.inspectorate.dto.response;


import gov.kh.mcr.inspectorate.enums.AttendanceStatus;
import gov.kh.mcr.inspectorate.enums.AttendeeRole;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttendeeResponse {

    private Integer attendeeId;
    private Integer officerId;
    private String officerCode;
    private String officerName;
    private String departmentName;
    private AttendeeRole role;
    private AttendanceStatus attendanceStatus;
    private LocalDateTime checkInTime;
    private String note;
}