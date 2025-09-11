package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardsRepository;
import com.example.bankcards.repository.TransactionsRepository;
import com.example.bankcards.util.CardNumberCrypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
public class TransactionsService {

    private final CardNumberCrypto cardNumberCrypto;
    private final CardsRepository cardsRepository;
    private final TransactionsRepository transactionsRepository;

    @Autowired
    public TransactionsService(
            CardNumberCrypto cardNumberCrypto,
            CardsRepository cardsRepository, TransactionsRepository transactionsRepository) {
        this.cardNumberCrypto = cardNumberCrypto;
        this.cardsRepository = cardsRepository;
        this.transactionsRepository = transactionsRepository;
    }

    @Transactional
    public void transfer(TransactionRequest request, Long userId) {
        if (request.getAmount().compareTo(new BigDecimal("0.01")) < 0) {
            throw new InvalidCurrencyAmount("Amount must be greater than or equal to 0.01");
        }
        if (request.getAmount().scale() > 2) {
            throw new InvalidCurrencyAmount("Amount must have at most 2 decimal places");
        }

        String encryptedFromCard = cardNumberCrypto.encrypt(request.getFromCard());
        String encryptedToCard = cardNumberCrypto.encrypt(request.getToCard());
        Optional<Card> fromCardOpt = cardsRepository.findByCardNumber(encryptedFromCard);
        Optional<Card> toCardOpt = cardsRepository.findByCardNumber(encryptedToCard);
        if (fromCardOpt.isEmpty() || toCardOpt.isEmpty()) {
            throw new CardNotFoundException("Card not found");
        }

        Card fromCard = fromCardOpt.get();
        Card toCard = toCardOpt.get();

        if (!fromCard.getOwner().getId().equals(userId) || !toCard.getOwner().getId().equals(userId)) {
            throw new NoAuthorityException("You do not have permission to transfer with these cards");
        }
        if (fromCard.getId().equals(toCard.getId()) || fromCard.getCardNumber().equals(toCard.getCardNumber())) {
            throw new SameCardTransactionException("Transfer from the card to itself is not possible");
        }

        BigDecimal balanceFromCard = fromCard.getBalance();
        BigDecimal balanceToCard = toCard.getBalance();
        BigDecimal newBalanceFromCard = balanceFromCard.subtract(request.getAmount());
        if (newBalanceFromCard.compareTo(BigDecimal.ZERO) < 0) {
            throw new CardInsufficientFunds("Insufficient funds");
        }

        fromCard.setBalance(newBalanceFromCard);
        BigDecimal newBalanceToCard = balanceToCard.add(request.getAmount());
        toCard.setBalance(newBalanceToCard);

        Transaction newTransaction = new Transaction(
                fromCard.getCardNumber(),
                toCard.getCardNumber(),
                request.getAmount(),
                new Date()
        );

        transactionsRepository.save(newTransaction);
    }

}
