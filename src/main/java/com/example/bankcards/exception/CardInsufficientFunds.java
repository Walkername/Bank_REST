package com.example.bankcards.exception;

public class CardInsufficientFunds extends RuntimeException {
    public CardInsufficientFunds(String message) {
        super(message);
    }
}
