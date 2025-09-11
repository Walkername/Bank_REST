package com.example.bankcards.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class TransactionRequest {

    @NotBlank(message = "The card you are transferring from must be indicated")
    @Size(min = 16, max = 16, message = "Card number must be exactly 16 digits")
    @Pattern(regexp = "^\\d+$", message = "Card number must contain only digits")
    private String fromCard;

    @NotBlank(message = "The card you are transferring to must be indicated")
    @Size(min = 16, max = 16, message = "Card number must be exactly 16 digits")
    @Pattern(regexp = "^\\d+$", message = "Card number must contain only digits")
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
