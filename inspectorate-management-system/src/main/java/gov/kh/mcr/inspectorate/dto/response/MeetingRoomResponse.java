package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums
        .MeetingRoomStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeetingRoomResponse {

    private Integer           roomId;
    private String            roomCode;
    private Integer           capacity;
    private String            location;

    private MeetingRoomStatus status;
    private String            statusLabel;

    private Integer           currentMeetingId;
    private String            currentMeetingTitle;
    private String            currentMeetingOrganizer;

    private String            imageUrl;
    private LocalDateTime      createdAt;
}