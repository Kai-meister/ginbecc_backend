package gov.kh.mcr.inspectorate.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    MEETING      ("MEETING",
            "ការប្រជុំ",
            "/meetings/"),

    DOCUMENT     ("DOCUMENT",
            "ឯកសារ",
            "/documents/"),

    ANNOUNCEMENT ("ANNOUNCEMENT",
            "សេចក្តីប្រកាស",
            "/announcements/"),

    SYSTEM       ("SYSTEM",
            "ប្រព័ន្ធ",
            "/profile");

    private final String code;
    private final String labelKh;
    private final String pathPrefix;

    // MEETING + 10  - /meetings/10
    // SYSTEM + null - /profile
    public String buildPath(
            Integer referenceId) {
        if (this == SYSTEM
                || referenceId == null) {
            return pathPrefix;
        }
        return pathPrefix + referenceId;
    }
}