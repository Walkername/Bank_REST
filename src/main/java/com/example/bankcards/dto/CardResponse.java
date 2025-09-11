package com.example.bankcards.dto;

import com.example.bankcards.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Card response DTO containing card details")
public class CardResponse {

    @Schema(description = "Unique identifier of the card", example = "1")
    private Long id;

    @Schema(description = "Identifier of the card owner", example = "2")
    private Long ownerId;

    @Schema(description = "Masked card number (16 digits)", example = "**** **** **** 5678")
    private String cardNumber;

    @Schema(description = "Expiry date in YYYY-MM-DD HH:MM:SS.SSS format", example = "2029-09-11 15:05:03.693")
    private String expiryDate;

    @Schema(description = "Current status of the card", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Current balance of the card", example = "174.15")
    private BigDecimal balance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
