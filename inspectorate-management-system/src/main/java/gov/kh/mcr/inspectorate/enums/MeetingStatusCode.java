package gov.kh.mcr.inspectorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingStatusCode {

    DRAFT       ("DRAFT",       "សេចក្តីព្រាង"),
    SCHEDULED   ("SCHEDULED",   "កំណត់ពេល"),
    CONFIRMED   ("CONFIRMED",   "បានបញ្ជាក់"),
    IN_PROGRESS ("IN_PROGRESS", "កំពុងប្រជុំ"),
    COMPLETED   ("COMPLETED",   "បានបញ្ចប់"),
    CANCELLED   ("CANCELLED",   "បានលុបចោល"),
    POSTPONED   ("POSTPONED",   "បានពន្យារ"),
    RESCHEDULED ("RESCHEDULED", "កំណត់ពេលឡើងវិញ");

    private final String code;
    private final String labelKh;

    // Ignore on conflict check
    public static boolean isIgnoredForConflict(
            String code) {
        return CANCELLED.code.equals(code)
                || COMPLETED.code.equals(code);
    }

    // Can add/remove attendees
    public static boolean canEditAttendees(
            String code) {
        return DRAFT.code.equals(code)
                || SCHEDULED.code.equals(code)
                || CONFIRMED.code.equals(code)
                || POSTPONED.code.equals(code)
                || RESCHEDULED.code.equals(code);
    }

    // Can update meeting info
    public static boolean canUpdate(String code) {
        return DRAFT.code.equals(code)
                || SCHEDULED.code.equals(code)
                || CONFIRMED.code.equals(code)
                || POSTPONED.code.equals(code)
                || RESCHEDULED.code.equals(code);
    }

    //  Can cancel
    public static boolean canCancel(String code) {
        return DRAFT.code.equals(code)
                || SCHEDULED.code.equals(code)
                || CONFIRMED.code.equals(code)
                || POSTPONED.code.equals(code);
    }

    // Is final state
    public static boolean isFinal(String code) {
        return COMPLETED.code.equals(code)
                || CANCELLED.code.equals(code);
    }
}