package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.MeetingType;
import lombok.*;
import java.time.*;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeetingResponse {

    private Integer          meetingId;
    private String           title;
    private String           description;
    private MeetingType      meetingType;
    private LocalDate        meetingDate;
    private LocalTime        startTime;
    private LocalTime        endTime;
    private String           meetingLink;
    private Integer          roomId;
    private String           roomCode;
    private String           roomLocation;

    private Integer          organizerId;
    private String           organizerName;
    private String           statusCode;
    private String           statusLabel;
    private Integer          totalAttendees;
    private Integer          attendedCount;
    private Integer          absentCount;
    private Integer          invitedCount;
    private List<AttendeeResponse> attendees;

    private LocalDateTime    createdAt;
    private LocalDateTime    updatedAt;
}