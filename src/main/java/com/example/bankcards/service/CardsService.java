package com.example.bankcards.service;

import com.example.bankcards.dto.CardNumberResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardsRepository;
import com.example.bankcards.specifications.CardSpecifications;
import com.example.bankcards.util.BankModelMapper;
import com.example.bankcards.util.CardNumberCrypto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
public class CardsService {

    private final CardsRepository cardsRepository;
    private final CardNumberCrypto cardNumberCrypto;
    private final BankModelMapper bankModelMapper;

    public CardsService(
            CardsRepository cardsRepository,
            CardNumberCrypto cardNumberCrypto,
            BankModelMapper bankModelMapper) {
        this.cardsRepository = cardsRepository;
        this.cardNumberCrypto = cardNumberCrypto;
        this.bankModelMapper = bankModelMapper;
    }

    public CardResponse findOne(Long id, Long userId) {
        Card card = checkAuthorityAndGet(id, userId);
        return createCardResponse(card, userId);
    }

    public CardNumberResponse getCardNumber(Long id, Long userId) {
        Card card = checkAuthorityAndGet(id, userId);
        String decryptedCardNumber = cardNumberCrypto.decrypt(card.getCardNumber());
        return new CardNumberResponse(decryptedCardNumber);
    }

    @Transactional
    public void requestBlocking(Long id, Long userId) {
        Card card = checkAuthorityAndGet(id, userId);
        card.setStatus(CardStatus.BLOCK_REQUESTED);
    }

    public BigDecimal getBalance(Long id, Long userId) {
        Card card = checkAuthorityAndGet(id, userId);
        return card.getBalance();
    }

    public PageResponse<CardResponse> getUserCards(
            Long userId,
            int page,
            int limit,
            String[] sort,
            CardStatus cardStatus,
            BigDecimal minBalance,
            BigDecimal maxBalance
    ) {
        Sort sorting = Sort.by(createOrders(sort));
        Pageable pageable = PageRequest.of(page, limit, sorting);

        Specification<Card> specification = Specification.allOf(CardSpecifications.byUserId(userId));

        if (cardStatus != null) {
            specification = specification.and(CardSpecifications.hasStatus(cardStatus));
        }
        if (minBalance != null) {
            specification = specification.and(CardSpecifications.balanceGreaterThan(minBalance));
        }
        if (maxBalance != null) {
            specification = specification.and(CardSpecifications.balanceLessThan(maxBalance));
        }

        Page<Card> cardsPage = cardsRepository.findAll(specification, pageable);
        List<CardResponse> cardResponses = new ArrayList<>();
        for (Card card : cardsPage.getContent()) {
            CardResponse cardResponse = createCardResponse(card, userId);
            cardResponses.add(cardResponse);
        }

        return new PageResponse<>(
                cardResponses,
                page,
                limit,
                cardsPage.getTotalElements(),
                cardsPage.getTotalPages()
        );
    }

    private CardResponse createCardResponse(Card card, Long userId) {
        CardResponse cardResponse = bankModelMapper.convertToCardResponse(card);
        String decryptedCardNumber = cardNumberCrypto.decrypt(card.getCardNumber());
        String maskedCardNumber = cardNumberCrypto.transformToMaskedNumber(decryptedCardNumber);
        cardResponse.setCardNumber(maskedCardNumber);
        cardResponse.setOwnerId(userId);
        return cardResponse;
    }

    private List<Sort.Order> createOrders(String[] sort) {
        return Arrays.stream(sort).map(this::parseSort).toList();
    }

    private Sort.Order parseSort(String sortParam) {
        String[] parts = sortParam.split(":");
        String property = parts[0];
        Sort.Direction direction = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.DESC;
        return new Sort.Order(direction, property);
    }

    private Card checkAuthorityAndGet(Long id, Long userId) {
        Optional<Card> card = cardsRepository.findById(id);
        if (card.isEmpty()) {
            throw new CardNotFoundException("Card not found");
        }
        Card cardPresent = card.get();
        if (!cardPresent.getOwner().getId().equals(userId)) {
            throw new NoAuthorityException("You do not have permission to access this card");
        }
        return cardPresent;
    }
}
