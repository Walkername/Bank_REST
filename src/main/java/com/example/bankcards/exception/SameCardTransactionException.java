package com.example.bankcards.exception;

public class SameCardTransactionException extends RuntimeException {
    public SameCardTransactionException(String message) {
        super(message);
    }
}
