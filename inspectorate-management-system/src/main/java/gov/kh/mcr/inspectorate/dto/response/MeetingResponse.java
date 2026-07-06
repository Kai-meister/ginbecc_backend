package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums
        .MeetingRoomStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeetingResponse {

    private Integer       meetingId;
    private String        title;
    private String        description;
    private String        meetingType;
    private LocalDate      meetingDate;
    private LocalTime      startTime;
    private LocalTime      endTime;

    // Room info
    private Integer       roomId;
    private String        roomCode;
    private String        roomLocation;

    private MeetingRoomStatus roomStatus;
    private String        roomStatusLabel;

    private String        organizerName;
    private String        organizerDept;
    private String        statusCode;
    private String        statusLabel;

    private Integer       durationMinutes;

    // Attendees
    @Builder.Default
    private Integer       totalAttendees = 0;
    @Builder.Default
    private Integer       attendedCount  = 0;
    @Builder.Default
    private Integer       absentCount    = 0;
    @Builder.Default
    private Integer       invitedCount   = 0;

    private List<AttendeeResponse> attendees;

    private LocalDateTime  createdAt;
    private LocalDateTime  updatedAt;
}