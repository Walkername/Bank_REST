package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Card number response DTO containing decrypted card number")
public class CardNumberResponse {

    @Schema(
            description = "Decrypted card number (16 digits)",
            example = "1234567812345678"
    )
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
