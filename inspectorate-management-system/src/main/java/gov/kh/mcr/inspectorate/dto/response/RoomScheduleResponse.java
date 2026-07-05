package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.MeetingRoomStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RoomScheduleResponse {

    private Integer       roomId;
    private String        roomCode;
    private MeetingRoomStatus status;
    private String        statusLabel;

    private List<RoomBookingSlot>
            bookings;
    private List<RoomTimeSlot>
            todaySlots;

    @Data
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class RoomBookingSlot {
        private Integer     meetingId;
        private String      meetingTitle;
        private String      organizerName;
        private LocalDate    date;
        private LocalTime    startTime;
        private LocalTime    endTime;
        private String       statusCode;
        private String       statusLabel;
        private boolean      isNow;
        private boolean      isPast;
        private boolean      isCancelled;
    }

    @Data
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class RoomTimeSlot {
        private LocalTime   startTime;
        private LocalTime   endTime;
        private LocalDate    date;
        // AVAILABLE / BOOKED / IN_USE
        private String       slotStatus;
        private String       slotStatusLabel;
        private Integer      meetingId;
        private String       meetingTitle;
    }
}