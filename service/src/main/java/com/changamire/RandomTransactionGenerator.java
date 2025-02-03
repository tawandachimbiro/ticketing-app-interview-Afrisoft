package com.changamire;


import java.util.Random;

public class RandomTransactionGenerator {

    private RandomTransactionGenerator() {

    }


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

