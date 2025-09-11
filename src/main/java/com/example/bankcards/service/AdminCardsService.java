package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.CardsRepository;
import com.example.bankcards.util.BankModelMapper;
import com.example.bankcards.util.CardNumberCrypto;
import com.example.bankcards.util.CardNumberGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AdminCardsService {

    private final CardsRepository cardsRepository;
    private final BankModelMapper bankModelMapper;
    private final AdminUsersService adminUsersService;
    private final CardNumberCrypto cardNumberCrypto;

    public AdminCardsService(
            CardsRepository cardsRepository,
            BankModelMapper bankModelMapper,
            AdminUsersService adminUsersService,
            @Value("${spring.card-number.encryption.secret}") String secret
    ) {
        this.cardsRepository = cardsRepository;
        this.bankModelMapper = bankModelMapper;
        this.adminUsersService = adminUsersService;
        this.cardNumberCrypto = new CardNumberCrypto(secret);
    }

    public CardResponse findOne(Long id) {
        Optional<Card> card = cardsRepository.findById(id);
        if (card.isEmpty()) {
            throw new CardNotFoundException("Card not found");
        }
        Card cardPresent = card.get();
        return createCardResponse(cardPresent, cardPresent.getOwner().getId());
    }

    @Transactional
    public void create(Long userId) {
        User owner = adminUsersService.findOne(userId);
        if (owner == null) {
            throw new UserNotFoundException("User not found");
        }

        String cardNumber = CardNumberGenerator.generateCardNumber();
        Date expiresAt = Date.from(ZonedDateTime.now().plusYears(4).toInstant());
        Card card = new Card(
                cardNumberCrypto.encrypt(cardNumber),
                owner,
                expiresAt,
                CardStatus.ACTIVE,
                BigDecimal.ZERO
        );

        cardsRepository.save(card);
    }

    @Transactional
    public void setStatus(Long id, CardStatus status) {
        Optional<Card> card = cardsRepository.findById(id);
        if (card.isEmpty()) {
            throw new CardNotFoundException("Card not found");
        }
        card.get().setStatus(status);
    }

    @Transactional
    public void deleteCard(Long id) {
        Optional<Card> card = cardsRepository.findById(id);
        if (card.isEmpty()) {
            throw new CardNotFoundException("Card not found");
        }
        cardsRepository.deleteById(id);
    }

    private CardResponse createCardResponse(Card card, Long userId) {
        CardResponse cardResponse = bankModelMapper.convertToCardResponse(card);
        String decryptedCardNumber = cardNumberCrypto.decrypt(card.getCardNumber());
        String maskedCardNumber = cardNumberCrypto.transformToMaskedNumber(decryptedCardNumber);
        cardResponse.setCardNumber(maskedCardNumber);
        cardResponse.setOwnerId(userId);
        return cardResponse;
    }

}
