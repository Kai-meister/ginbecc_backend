package gov.kh.mcr.inspectorate.util;


public final class StringUtils {

    private StringUtils() {}

    public static String safe(String s) {
        return s != null ? s : "";
    }

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static String truncate(String s, int maxLength) {
        if (s == null) return "";
        return s.length() <= maxLength
                ? s
                : s.substring(0, maxLength) + "...";
    }

    public static String toSnakeCase(String camelCase) {
        if (camelCase == null) return "";
        return camelCase
                .replaceAll("([A-Z])", "_$1")
                .toLowerCase()
                .replaceAll("^_", "");
    }
}
