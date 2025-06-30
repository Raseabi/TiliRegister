package com.tiliregister.app.model;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String DIGITS = "0123456789";
    private static final String SPECIALS = "!@#$%^&*()-_=+";
    private static final String PASSWORD_ALLOW = CHAR_LOWER + CHAR_UPPER + DIGITS + SPECIALS;

    private static final SecureRandom random = new SecureRandom();

    public static String generatePassword(int length) {
        if (length < 6) {
            throw new IllegalArgumentException("Password length must be at least 6 characters");
        }

        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(PASSWORD_ALLOW.length());
            password.append(PASSWORD_ALLOW.charAt(index));
        }

        return password.toString();
    }

}
