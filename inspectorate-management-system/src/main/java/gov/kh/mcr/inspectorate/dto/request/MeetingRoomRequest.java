package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.RoomStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingRoomRequest {

    @NotBlank(message = "កូដចាំបាច់")
    @Size(max = 20)
    private String roomCode;

    @Size(max = 255)
    private String location;

    @Min(value = 1, message = "សមត្ថភាពអប្បបរមា 1")
    private Integer capacity;

    @NotNull(message = "ស្ថានភាពចាំបាច់")
    private RoomStatus status;

    private String  facilities;
    private Integer imageAttachmentId;
}