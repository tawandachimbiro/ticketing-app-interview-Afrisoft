package com.changamire;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.security.SecureRandom;

/**
 * Alphanumeric ID Generator
 * 
 * This custom Hibernate ID generator creates secure alphanumeric identifiers
 * for tickets. Generates 12-character random IDs using uppercase letters and digits.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public class AlphaNumericIdGenerator implements IdentifierGenerator {
    @Override
    public String generate(SharedSessionContractImplementor session, Object object) {
        // Implement logic (e.g., UUID-based, SecureRandom, etc.)
        return generateAlphaNumericId(12);
    }

    private String generateAlphaNumericId(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}