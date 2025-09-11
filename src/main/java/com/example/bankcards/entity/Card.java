package com.example.bankcards.entity;

import com.example.bankcards.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "card")
@Schema(name = "Card", description = "Card entity representing a bank card with financial information")
public class Card {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the card", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "card_number")
    @Schema(
            description = "Encrypted card number (16 digits)",
            example = "encrypted_card_number_1234",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String cardNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @Schema(description = "User who owns this card", implementation = User.class, accessMode = Schema.AccessMode.READ_ONLY)
    private User owner;

    @Column(name = "expiry_date")
    @Schema(
            description = "Expiration date of the card",
            example = "2025-12-31T23:59:59.999Z",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Date expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Schema(
            description = "Current status of the card",
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "BLOCKED", "EXPIRED", "INACTIVE", "BLOCK_REQUESTED"},
            defaultValue = "ACTIVE"
    )
    private CardStatus status;

    @Column(name = "balance")
    @Schema(
            description = "Current balance on the card",
            example = "1500.75",
            minimum = "0.00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private BigDecimal balance;

    public Card() {
    }

    public Card(String cardNumber, User owner, Date expiryDate, CardStatus status, BigDecimal balance) {
        this.cardNumber = cardNumber;
        this.owner = owner;
        this.expiryDate = expiryDate;
        this.status = status;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
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
