package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.MeetingType;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingResponse {

    private Integer       meetingId;
    private String        title;
    private LocalDate     meetingDate;
    private LocalTime     startTime;
    private LocalTime     endTime;
    private MeetingType meetingType;
    private String        meetingLink;
    private Integer       roomId;
    private String        roomCode;
    private String        roomLocation;
    private Integer       organizerId;
    private String        organizerName;
    private String        statusCode;
    private String        statusLabel;
    private String        agenda;
    private String        note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}