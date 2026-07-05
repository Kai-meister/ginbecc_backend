package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeetingReportResponse {

    private Integer   no;
    private String    title;
    @Builder.Default
    private String    meetingType    = "";
    private LocalDate meetingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    @Builder.Default
    private String    roomCode       = "Online";
    @Builder.Default
    private String    organizerName  = "";
    @Builder.Default
    private Integer   totalAttendees = 0;
    @Builder.Default
    private Integer   attendedCount  = 0;
    @Builder.Default
    private Integer   absentCount    = 0;
    @Builder.Default
    private String    statusCode     = "";
    @Builder.Default
    private String    statusLabel    = "";
}