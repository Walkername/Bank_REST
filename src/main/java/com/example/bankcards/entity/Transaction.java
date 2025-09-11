package com.example.bankcards.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transaction")
@Schema(name = "Transaction", description = "Transaction entity representing a fund transfer between cards")
public class Transaction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the transaction", example = "1")
    private Long id;

    @Column(name = "from_card_number")
    @Schema(description = "Encrypted source card number", example = "encrypted_card_number_1")
    private String fromCardNumber;

    @Column(name = "to_card_number")
    @Schema(description = "Encrypted destination card number", example = "encrypted_card_number_2")
    private String toCardNumber;

    @Column(name = "amount")
    @Schema(description = "Amount transferred", example = "100.50")
    private BigDecimal amount;

    @Column(name = "executed_at")
    @Schema(description = "Date and time when transaction occurred", example = "2025-09-11 17:11:37.923000")
    private Date executedAt;

    public Transaction() {
    }

    public Transaction(String fromCardNumber, String toCardNumber, BigDecimal amount, Date executedAt) {
        this.fromCardNumber = fromCardNumber;
        this.toCardNumber = toCardNumber;
        this.amount = amount;
        this.executedAt = executedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromCardNumber() {
        return fromCardNumber;
    }

    public void setFromCardNumber(String fromCardNumber) {
        this.fromCardNumber = fromCardNumber;
    }

    public String getToCardNumber() {
        return toCardNumber;
    }

    public void setToCardNumber(String toCardNumber) {
        this.toCardNumber = toCardNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Date executedAt) {
        this.executedAt = executedAt;
    }
}
