package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardsRepository;
import com.example.bankcards.repository.TransactionsRepository;
import com.example.bankcards.util.CardNumberCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TransactionsServiceTest {

    @Mock
    private CardNumberCrypto cardNumberCrypto;

    @Mock
    private CardsRepository cardsRepository;

    @Mock
    private TransactionsRepository transactionsRepository;

    @InjectMocks
    private TransactionsService transactionsService;

    private Card fromCard;
    private Card toCard;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("John Doe");

        fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setCardNumber("encryptedFrom");
        fromCard.setOwner(user);
        fromCard.setBalance(new BigDecimal("100.00"));

        toCard = new Card();
        toCard.setId(2L);
        toCard.setCardNumber("encryptedTo");
        toCard.setOwner(user);
        toCard.setBalance(new BigDecimal("50.00"));
    }

    @Test
    void transfer_successful() {
        TransactionRequest request = new TransactionRequest();
        request.setFromCard("1111");
        request.setToCard("2222");
        request.setAmount(new BigDecimal("30.00"));

        when(cardNumberCrypto.encrypt("1111")).thenReturn("encryptedFrom");
        when(cardNumberCrypto.encrypt("2222")).thenReturn("encryptedTo");
        when(cardsRepository.findByCardNumber("encryptedFrom")).thenReturn(Optional.of(fromCard));
        when(cardsRepository.findByCardNumber("encryptedTo")).thenReturn(Optional.of(toCard));

        transactionsService.transfer(request, 1L);

        assertEquals(new BigDecimal("70.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("80.00"), toCard.getBalance());
        verify(transactionsRepository).save(any(Transaction.class));
    }

    @Test
    void transfer_insufficientFunds() {
        TransactionRequest request = new TransactionRequest();
        request.setFromCard("1111");
        request.setToCard("2222");
        request.setAmount(new BigDecimal("150.00"));

        when(cardNumberCrypto.encrypt(anyString())).thenAnswer(invocation -> {
            String arg = invocation.getArgument(0);
            return arg.equals("1111") ? "encryptedFrom" : "encryptedTo";
        });
        when(cardsRepository.findByCardNumber("encryptedFrom")).thenReturn(Optional.of(fromCard));
        when(cardsRepository.findByCardNumber("encryptedTo")).thenReturn(Optional.of(toCard));

        assertThrows(CardInsufficientFunds.class,
                () -> transactionsService.transfer(request, 1L));
    }

    @Test
    void transfer_invalidAmount_zero() {
        TransactionRequest request = new TransactionRequest();
        request.setFromCard("1111");
        request.setToCard("2222");
        request.setAmount(new BigDecimal("0.0"));

        assertThrows(InvalidCurrencyAmount.class,
                () -> transactionsService.transfer(request, 1L));
    }

    @Test
    void transfer_invalidAmount_tooManyDecimals() {
        TransactionRequest request = new TransactionRequest();
        request.setFromCard("1111");
        request.setToCard("2222");
        request.setAmount(new BigDecimal("10.123"));

        assertThrows(InvalidCurrencyAmount.class,
                () -> transactionsService.transfer(request, 1L));
    }

    @Test
    void transfer_cardNotFound() {
        TransactionRequest request = new TransactionRequest();
        request.setFromCard("1111");
        request.setToCard("2222");
        request.setAmount(new BigDecimal("10.00"));

        when(cardNumberCrypto.encrypt(anyString())).thenReturn("encryptedFrom");
        when(cardsRepository.findByCardNumber("encryptedFrom")).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class,
                () -> transactionsService.transfer(request, 1L));
    }

    @Test
    void transfer_noAuthority() {
        TransactionRequest request = new TransactionRequest();
        request.setFromCard("1111");
        request.setToCard("2222");
        request.setAmount(new BigDecimal("10.00"));

        User otherUser = new User();
        otherUser.setId(99L);
        fromCard.setOwner(otherUser);

        when(cardNumberCrypto.encrypt(anyString())).thenReturn("encryptedFrom");
        when(cardsRepository.findByCardNumber("encryptedFrom")).thenReturn(Optional.of(fromCard));
        when(cardsRepository.findByCardNumber("encryptedTo")).thenReturn(Optional.of(toCard));

        assertThrows(NoAuthorityException.class,
                () -> transactionsService.transfer(request, 1L));
    }

    @Test
    void transfer_sameCard() {
        TransactionRequest request = new TransactionRequest();
        request.setFromCard("1111");
        request.setToCard("1111");
        request.setAmount(new BigDecimal("10.00"));

        when(cardNumberCrypto.encrypt("1111")).thenReturn("encryptedFrom");
        when(cardsRepository.findByCardNumber("encryptedFrom")).thenReturn(Optional.of(fromCard));

        assertThrows(SameCardTransactionException.class,
                () -> transactionsService.transfer(request, 1L));
    }
}
