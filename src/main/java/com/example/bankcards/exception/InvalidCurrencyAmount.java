package com.example.bankcards.exception;

public class InvalidCurrencyAmount extends RuntimeException {
  public InvalidCurrencyAmount(String message) {
    super(message);
  }
}
