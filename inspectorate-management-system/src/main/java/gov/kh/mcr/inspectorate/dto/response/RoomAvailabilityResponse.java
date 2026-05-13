package gov.kh.mcr.inspectorate.dto.response;


import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAvailabilityResponse {

    private Integer             roomId;
    private String              roomCode;
    private String              roomLocation;
    private LocalDate           meetingDate;
    private LocalTime           requestedStart;
    private LocalTime           requestedEnd;
    private List<ConflictInfo>  conflicts;
    private List<AvailableSlot> availableSlots;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ConflictInfo {
        private Integer   meetingId;
        private String    title;
        private LocalTime startTime;
        private LocalTime endTime;
        private String    organizerName;
        private String    statusCode;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AvailableSlot {
        private LocalTime from;
        private LocalTime to;
        private long      durationMinutes;
    }
}
