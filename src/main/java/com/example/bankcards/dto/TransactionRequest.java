package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public class TransactionRequest {

    private String fromCard;

    private String toCard;

    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @Digits(integer = 19, fraction = 2, message = "Amount must have at most 2 decimal places")
    private BigDecimal amount;

    public String getFromCard() {
        return fromCard;
    }

    public void setFromCard(String fromCard) {
        this.fromCard = fromCard;
    }

    public String getToCard() {
        return toCard;
    }

    public void setToCard(String toCard) {
        this.toCard = toCard;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
