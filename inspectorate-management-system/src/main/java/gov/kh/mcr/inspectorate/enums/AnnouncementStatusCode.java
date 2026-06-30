package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnnouncementStatusCode {

    DRAFT       ("DRAFT",
            "សេចក្តីព្រាង"),
    PUBLISHED   ("PUBLISHED",
            "បានផ្សព្វផ្សាយ"),
    SCHEDULED   ("SCHEDULED",
            "កំណត់ពេល"),
    ARCHIVED    ("ARCHIVED",
            "ប័ណ្ណសារ"),
    CANCELLED   ("CANCELLED",
            "បានលុបចោល");

    private final String code;
    private final String labelKh;

//    public static boolean isPublished(String code) {
//        return PUBLISHED.code.equals(code);
//    }
//
//    public static boolean isDraft(String code) {
//        return DRAFT.code.equals(code);
//    }
//
//    public static boolean isVisible(String code) {
//        return PUBLISHED.code.equals(code)
//                || SCHEDULED.code.equals(code);
//    }
}