package com.example.bankcards.exception;

public class WrongCardStatus extends RuntimeException {
    public WrongCardStatus(String message) {
        super(message);
    }
}
