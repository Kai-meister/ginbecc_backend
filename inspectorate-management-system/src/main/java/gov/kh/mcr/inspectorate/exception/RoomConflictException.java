package gov.kh.mcr.inspectorate.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RoomConflictException extends ApiException {

    private final String roomCode;
    private final String conflictTitle;
    private final String conflictTime;

    public RoomConflictException(String roomCode,
                                 String conflictTitle,
                                 String conflictTime) {
        super(HttpStatus.CONFLICT,
                "ROOM_CONFLICT",
                "បន្ទប់ [" + roomCode + "] ត្រូវបានកក់ដោយ: ["
                        + conflictTitle + "] ម៉ោង " + conflictTime);
        this.roomCode      = roomCode;
        this.conflictTitle = conflictTitle;
        this.conflictTime  = conflictTime;
    }
}