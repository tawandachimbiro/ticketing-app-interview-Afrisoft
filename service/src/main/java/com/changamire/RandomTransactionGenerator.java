package com.changamire;


import java.util.Random;

/**
 * Random Transaction Generator Utility
 * <p>
 * This utility class generates random 10-digit numeric strings for transaction
 * reference numbers used in payment processing.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public class RandomTransactionGenerator {

    private RandomTransactionGenerator() {

    }

    /**
     * Generates a random 10-digit numeric string.
     * 
     * @return A string containing 10 random digits
     */
    public static String generateRandomNumbers() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 10;
        while (length-- > 0) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}

