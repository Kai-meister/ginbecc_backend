package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttachmentRefType {

    OFFICER_PROFILE(
            "OFFICER_PROFILE",
            "មន្ត្រីរាជការ",
            "officers"
    ),

    CONTRACT_OFFICER_PROFILE(
            "CONTRACT_OFFICER_PROFILE",
            "រូបភាព ContractOfficer",
            "contract-officer-profiles"
    ),

    DOCUMENT(
            "DOCUMENT",
            "ឯកសារ",
            "documents"
    ),

    MEETING_ROOM(
            "MEETING_ROOM",
            "បន្ទប់ប្រជុំ",
            "meeting-rooms"
    ),

    MEETING_MINUTE(
            "MEETING_MINUTE",
            "កំណត់ហេតុប្រជុំ",
            "meeting-minutes"
    ),

    ANNOUNCEMENT(
            "ANNOUNCEMENT",
            "សេចក្តីប្រកាស",
            "announcements"
    );

    private final String code;
    private final String labelKh;
    private final String folder;

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

    public String toPathPrefix() {
        return this.code
                .toLowerCase()
                .replace("_", "-");
    }

    public static String[] allCodes() {
        AttachmentRefType[] types = values();
        String[] codes = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            codes[i] = types[i].code;
        }
        return codes;
    }
}
