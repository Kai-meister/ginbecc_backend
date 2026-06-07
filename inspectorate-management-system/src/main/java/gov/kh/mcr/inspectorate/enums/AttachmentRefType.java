package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttachmentRefType {

    OFFICER          ("OFFICER",
            "មន្ត្រីរាជការ"),
    USER             ("USER",
            "អ្នកប្រើប្រាស់"),
    DOCUMENT         ("DOCUMENT",
            "ឯកសារ"),
    MEETING_ROOM     ("MEETING_ROOM",
            "បន្ទប់ប្រជុំ"),
    MEETING_MINUTE   ("MEETING_MINUTE",
            "កំណត់ហេតុប្រជុំ"),
    ANNOUNCEMENT     ("ANNOUNCEMENT",
            "សេចក្តីប្រកាស");

    private final String code;
    private final String labelKh;

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        for (AttachmentRefType t : values()) {
            if (t.code.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public static AttachmentRefType fromCode(
            String code) {
        if (code == null) {
            throw new IllegalArgumentException(
                    "AttachmentRefType code null");
        }
        for (AttachmentRefType t : values()) {
            if (t.code.equalsIgnoreCase(code)) {
                return t;
            }
        }
        throw new IllegalArgumentException(
                "AttachmentRefType មិនស្គាល់: " + code);
    }

    //  MinIO path prefix
    // MEETING_ROOM to "meeting-room"
    public String toPathPrefix() {
        return this.code
                .toLowerCase()
                .replace("_", "-");
    }

    // All valid codes
    public static String[] allCodes() {
        AttachmentRefType[] types = values();
        String[] codes = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            codes[i] = types[i].code;
        }
        return codes;
    }

    // Regex pattern សម្រាប់ @Pattern
    public static final String VALIDATION_PATTERN =
            "^(OFFICER|CONTRACT_OFFICER|USER"
                    + "|DOCUMENT|MEETING_ROOM"
                    + "|MEETING_MINUTE|ANNOUNCEMENT)$";
}