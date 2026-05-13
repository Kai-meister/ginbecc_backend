package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatusCode {

    ACTIVE      ("ACTIVE",
            "សកម្ម"),
    BLOCKED     ("BLOCKED",
            "ត្រូវបានបិទ"),
    SUSPENDED   ("SUSPENDED",
            "ផ្អាកបណ្ដោះអាសន្ន"),
    INACTIVE    ("INACTIVE",
            "មិនសកម្ម"),
    LOCKED      ("LOCKED",
            "បានចាក់សោ");

    private final String code;
    private final String labelKh;

    public static boolean isActive(String code) {
        return ACTIVE.code.equals(code);
    }

    public static boolean isLoginAllowed(
            String code) {
        return ACTIVE.code.equals(code);
    }

    public static boolean isLoginBlocked(
            String code) {
        return BLOCKED.code.equals(code)
                || SUSPENDED.code.equals(code)
                || INACTIVE.code.equals(code)
                || LOCKED.code.equals(code);
    }
}
