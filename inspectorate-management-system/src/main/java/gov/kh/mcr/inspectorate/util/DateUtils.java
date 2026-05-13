package gov.kh.mcr.inspectorate.util;

import java.time.LocalDate;
import java.time.Period;

public final class DateUtils {

    private DateUtils() {}

    public static int calculateAge(LocalDate dob) {
        if (dob == null) return 0;
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public static long daysUntil(LocalDate futureDate) {
        if (futureDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS
                .between(LocalDate.now(), futureDate);
    }

    public static boolean isExpired(LocalDate expiryDate) {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now());
    }

    public static boolean isExpiringSoon(
            LocalDate expiryDate, int withinDays) {
        if (expiryDate == null) return false;
        LocalDate threshold = LocalDate.now().plusDays(withinDays);
        return !expiryDate.isBefore(LocalDate.now())
                && !expiryDate.isAfter(threshold);
    }
}
