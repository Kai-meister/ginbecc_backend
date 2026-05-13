package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OfficerStatusCode {

    ACTIVE              ("ACTIVE",
            "កំពុងបម្រើការ"),
    INACTIVE            ("INACTIVE",
            "មិនសកម្ម"),
    RETIRED             ("RETIRED",
            "ចូលនិវត្តន៍"),
    SUSPENDED           ("SUSPENDED",
            "ផ្អាកការងារ"),
    ON_LEAVE            ("ON_LEAVE",
            "ឈប់សម្រាក"),
    PROBATION           ("PROBATION",
            "សាកល្បង"),
    RESIGNED            ("RESIGNED",
            "បានលាឈប់"),
    CONTRACT_EXPIRED    ("CONTRACT_EXPIRED",
            "កិច្ចសន្យាផុតកំណត់");

    private final String code;
    private final String labelKh;

    public static boolean isActive(String code) {
        return ACTIVE.code.equals(code);
    }

    public static boolean isExpired(String code) {
        return CONTRACT_EXPIRED.code.equals(code);
    }
}