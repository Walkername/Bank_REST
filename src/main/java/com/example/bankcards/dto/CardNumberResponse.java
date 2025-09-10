package com.example.bankcards.dto;

public class CardNumberResponse {

    private String cardNumber;

    public CardNumberResponse(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
