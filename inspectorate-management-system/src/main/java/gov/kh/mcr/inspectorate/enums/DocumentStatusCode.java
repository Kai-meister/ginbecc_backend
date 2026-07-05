package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentStatusCode {

    DRAFT       ("DRAFT",    "សេចក្តីព្រាង"),
    PENDING     ("PENDING",  "រង់ចាំអនុម័ត"),
    APPROVED    ("APPROVED", "បានអនុម័ត"),
    REJECTED    ("REJECTED", "បានបដិសេធ"),
    EXPIRED     ("EXPIRED",  "ផុតកំណត់"),
    ARCHIVED    ("ARCHIVED", "ប័ណ្ណសារ"),
    CANCELLED   ("CANCELLED","បានលុបចោល");

    private final String code;
    private final String labelKh;

    public static boolean isDraft(String code) {
        return DRAFT.code.equals(code);
    }

    public static boolean isPending(String code) {
        return PENDING.code.equals(code);
    }

    public static boolean isApproved(String code) {
        return APPROVED.code.equals(code);
    }

//    public static boolean isRejected(String code) {
//        return REJECTED.code.equals(code);
//    }
//
//    public static boolean isFinal(String code) {
//        return APPROVED.code.equals(code)
//                || REJECTED.code.equals(code)
//                || EXPIRED.code.equals(code);
//    }
}
