package com.example.bankcards.util;

import java.util.Random;

public class CardNumberGenerator {

    public static Random random = new Random();

    public static String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(9));
        }
        return cardNumber.toString();
    }

}
