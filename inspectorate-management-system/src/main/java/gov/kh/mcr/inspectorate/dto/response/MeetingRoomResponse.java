package gov.kh.mcr.inspectorate.dto.response;
import gov.kh.mcr.inspectorate.enums.RoomStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomResponse {

    private Integer    roomId;
    private String     roomCode;
    private String     location;
    private Integer    capacity;
    private RoomStatus status;
    private String     facilities;
    private String     imageUrl;
}
