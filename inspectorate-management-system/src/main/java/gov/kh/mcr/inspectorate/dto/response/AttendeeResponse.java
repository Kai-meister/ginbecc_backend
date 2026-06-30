// AttendeeResponse.java — Fix
package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums
        .AttendanceStatus;
import gov.kh.mcr.inspectorate.enums
        .AttendeeRole;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttendeeResponse {

    private Integer          attendeeId;
    private Integer          meetingId;
    private String           meetingTitle;
    private Integer          userId;
    private String           userName;
    private String           departmentName;

    private AttendeeRole     role;
    private String           roleLabel;
    private AttendanceStatus attendanceStatus;
    private String           attendanceLabel;
    private LocalDateTime    checkInTime;
    private String           note;
    private LocalDateTime    createdAt;
}