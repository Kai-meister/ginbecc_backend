package gov.kh.mcr.inspectorate.util;

import java.security.SecureRandom;
import java.util.*;

public final class PasswordGenerator {

    private PasswordGenerator() {}

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private static final String UPPER  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER  = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "@#$%!&*";
    private static final String ALL =
            UPPER + LOWER + DIGITS + SPECIAL;

    public static String generate() {
        return generate(12);
    }

    public static String generate(int length) {
        if (length < 8) {
            throw new IllegalArgumentException(
                    "Password length minimum 8");
        }

        List<Character> chars = new ArrayList<>();

        chars.add(pickRandom(UPPER));
        chars.add(pickRandom(UPPER));
        chars.add(pickRandom(LOWER));
        chars.add(pickRandom(LOWER));
        chars.add(pickRandom(DIGITS));
        chars.add(pickRandom(DIGITS));
        chars.add(pickRandom(SPECIAL));
        chars.add(pickRandom(SPECIAL));


        for (int i = 8; i < length; i++) {
            chars.add(pickRandom(ALL));
        }

        Collections.shuffle(chars, RANDOM);

        StringBuilder sb = new StringBuilder();
        chars.forEach(sb::append);
        return sb.toString();
    }

    private static char pickRandom(String source) {
        return source.charAt(
                RANDOM.nextInt(source.length()));
    }


    public static boolean isStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper   = false;
        boolean hasLower   = false;
        boolean hasDigit   = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c))     hasDigit = true;
            if (SPECIAL.indexOf(c) >= 0)  hasSpecial = true;
        }
        return hasUpper && hasLower
                && hasDigit && hasSpecial;
    }
}