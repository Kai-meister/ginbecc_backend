package gov.kh.mcr.inspectorate.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingStatusCode {

    DRAFT           ("DRAFT",
            "សេចក្តីព្រាង"),
    SCHEDULED       ("SCHEDULED",
            "កំណត់ពេល"),
    CONFIRMED       ("CONFIRMED",
            "បានបញ្ជាក់"),
    IN_PROGRESS     ("IN_PROGRESS",
            "កំពុងប្រជុំ"),
    COMPLETED       ("COMPLETED",
            "បានបញ្ចប់"),
    CANCELLED       ("CANCELLED",
            "បានលុបចោល"),
    POSTPONED       ("POSTPONED",
            "បានពន្យារ"),
    RESCHEDULED     ("RESCHEDULED",
            "កំណត់ពេលឡើងវិញ");

    private final String code;
    private final String labelKh;

    public static boolean isIgnoredForConflict(
            String code) {
        return CANCELLED.code.equals(code)
                || COMPLETED.code.equals(code);
    }

    public static boolean isActive(String code) {
        return SCHEDULED.code.equals(code)
                || CONFIRMED.code.equals(code)
                || IN_PROGRESS.code.equals(code);
    }

    public static boolean isCancellable(
            String code) {
        return DRAFT.code.equals(code)
                || SCHEDULED.code.equals(code)
                || CONFIRMED.code.equals(code)
                || POSTPONED.code.equals(code);
    }
}