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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminCardsServiceTest {

    @Mock
    private CardsRepository cardsRepository;

    @Mock
    private BankModelMapper bankModelMapper;

    @Mock
    private CardNumberCrypto cardNumberCrypto;

    @Mock
    private AdminUsersService adminUsersService;

    @InjectMocks
    private AdminCardsService adminCardsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminCardsService = new AdminCardsService(cardsRepository, bankModelMapper, adminUsersService, cardNumberCrypto);
    }

    @Test
    void findOne_success() {
        User user = new User();
        user.setId(2L);

        Card card = new Card("encrypted-number", user, new Date(), CardStatus.ACTIVE, BigDecimal.TEN);
        card.setId(1L);

        CardResponse response = new CardResponse();
        response.setId(1L);

        when(cardsRepository.findById(1L)).thenReturn(Optional.of(card));
        when(bankModelMapper.convertToCardResponse(card)).thenReturn(response);

        CardResponse result = adminCardsService.findOne(1L);

        assertThat(result.getId()).isEqualTo(1L);
        verify(cardsRepository).findById(1L);
    }

    @Test
    void findOne_notFound() {
        when(cardsRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCardsService.findOne(99L))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void create_success() {
        User user = new User();
        user.setId(5L);

        when(adminUsersService.findOne(5L)).thenReturn(user);

        adminCardsService.create(5L);

        verify(cardsRepository).save(any(Card.class));
    }

    @Test
    void create_userNotFound() {
        when(adminUsersService.findOne(99L)).thenReturn(null);

        assertThatThrownBy(() -> adminCardsService.create(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void setStatus_success() {
        Card card = new Card();
        card.setId(1L);

        when(cardsRepository.findById(1L)).thenReturn(Optional.of(card));

        adminCardsService.setStatus(1L, CardStatus.BLOCKED);

        assertThat(card.getStatus()).isEqualTo(CardStatus.BLOCKED);
    }

    @Test
    void setStatus_cardNotFound() {
        when(cardsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCardsService.setStatus(1L, CardStatus.ACTIVE))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void deleteCard_success() {
        Card card = new Card();
        card.setId(1L);

        when(cardsRepository.findById(1L)).thenReturn(Optional.of(card));

        adminCardsService.deleteCard(1L);

        verify(cardsRepository).deleteById(1L);
    }

    @Test
    void deleteCard_notFound() {
        when(cardsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminCardsService.deleteCard(1L))
                .isInstanceOf(CardNotFoundException.class);
    }
}
