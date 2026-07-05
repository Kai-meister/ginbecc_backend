package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingRoomStatus {

    // ទំនេរ — អាចកក់ + ប្រើបាន
    AVAILABLE(
            "AVAILABLE",
            "ទំនេរ"),

    // កំពុងប្រជុំ — Auto ពេល Meeting
    // ចាប់ផ្ដើម
    IN_USE(
            "IN_USE",
            "កំពុងប្រជុំ"),

    // ជួសជុល — Admin Manual Set
    MAINTENANCE(
            "MAINTENANCE",
            "ជួសជុល"),

    // បិទ — Admin Manual Set
    CLOSED(
            "CLOSED",
            "បិទ");

    private final String code;
    private final String labelKh;

    // Fix — ត្រួតពិនិត្យ ថា Room
    // Bookable ដែរឬទេ
    public boolean isBookable() {
        return this == AVAILABLE;
    }

    public boolean isActive() {
        return this == AVAILABLE
                || this == IN_USE;
    }
}