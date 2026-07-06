package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingRoomStatus {

    AVAILABLE(
            "AVAILABLE",
            "ទំនេរ"),
    IN_USE(
            "IN_USE",
            "កំពុងប្រជុំ"),

    MAINTENANCE(
            "MAINTENANCE",
            "ជួសជុល"),

    CLOSED(
            "CLOSED",
            "បិទ");

    private final String code;
    private final String labelKh;


    public boolean isBookable() {
        return this == AVAILABLE;
    }

    public boolean isActive() {
        return this == AVAILABLE
                || this == IN_USE;
    }
}