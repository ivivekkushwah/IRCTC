package org.Spring.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.Objects;

public final class UserServiceUtil {

    // Recommended cost factor (12–14 for most apps)
    private static final int BCRYPT_COST = 12;

    // Prevent instantiation
    private UserServiceUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Hashes a plain text password using BCrypt.
     */
    public static String hashPassword(String plainPassword) {
        Objects.requireNonNull(plainPassword, "Password must not be null");

        return BCrypt.withDefaults()
                .hashToString(BCRYPT_COST, plainPassword.toCharArray());
    }

    /**
     * Verifies a plain password against a hashed password.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        Objects.requireNonNull(plainPassword, "Password must not be null");
        Objects.requireNonNull(hashedPassword, "Hashed password must not be null");

        BCrypt.Result result = BCrypt.verifyer()
                .verify(plainPassword.toCharArray(), hashedPassword);

        return result.verified;
    }
}
