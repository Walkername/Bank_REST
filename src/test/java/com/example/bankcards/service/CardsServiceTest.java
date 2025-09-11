package com.example.bankcards.service;

import com.example.bankcards.dto.CardNumberResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.NoAuthorityException;
import com.example.bankcards.repository.CardsRepository;
import com.example.bankcards.util.BankModelMapper;
import com.example.bankcards.util.CardNumberCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardsServiceTest {

    private CardsRepository cardsRepository;
    private CardNumberCrypto cardNumberCrypto;
    private BankModelMapper bankModelMapper;
    private CardsService cardsService;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() {
        cardsRepository = mock(CardsRepository.class);
        cardNumberCrypto = mock(CardNumberCrypto.class);
        bankModelMapper = mock(BankModelMapper.class);

        cardsService = new CardsService(cardsRepository, cardNumberCrypto, bankModelMapper);

        user = new User();
        user.setId(1L);

        card = new Card();
        card.setId(1L);
        card.setOwner(user);
        card.setCardNumber("encrypted");
        card.setBalance(BigDecimal.valueOf(1000));
        card.setStatus(CardStatus.ACTIVE);
    }

    @Test
    void findOne_success() {
        when(cardsRepository.findById(1L)).thenReturn(Optional.of(card));
        when(bankModelMapper.convertToCardResponse(card)).thenReturn(new CardResponse());

        CardResponse response = cardsService.findOne(1L, user.getId());

        assertNotNull(response);
        verify(cardsRepository, times(1)).findById(1L);
    }

    @Test
    void findOne_cardNotFound() {
        when(cardsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardsService.findOne(1L, user.getId()));
    }

    @Test
    void findOne_noAuthority() {
        User otherUser = new User();
        otherUser.setId(2L);
        card.setOwner(otherUser);
        when(cardsRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(NoAuthorityException.class, () -> cardsService.findOne(1L, user.getId()));
    }

    @Test
    void getCardNumber_success() {
        when(cardsRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardNumberCrypto.decrypt("encrypted")).thenReturn("1234567812345678");

        CardNumberResponse response = cardsService.getCardNumber(1L, user.getId());
        assertEquals("1234567812345678", response.getCardNumber());
    }

    @Test
    void requestBlocking_success() {
        when(cardsRepository.findById(1L)).thenReturn(Optional.of(card));

        cardsService.requestBlocking(1L, user.getId());
        assertEquals(CardStatus.BLOCK_REQUESTED, card.getStatus());
    }

    @Test
    void getBalance_success() {
        when(cardsRepository.findById(1L)).thenReturn(Optional.of(card));

        BigDecimal balance = cardsService.getBalance(1L, user.getId());
        assertEquals(BigDecimal.valueOf(1000), balance);
    }
}
