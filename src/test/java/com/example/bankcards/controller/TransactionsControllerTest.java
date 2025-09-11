package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.exception.InvalidCurrencyAmount;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.TokenService;
import com.example.bankcards.service.TransactionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionsController.class)
class TransactionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionsService transactionsService;

    @MockitoBean
    TokenService tokenService;

    private UserPrincipal user;

    @BeforeEach
    void setUp() {
        user = new UserPrincipal(1L, "test_user", "ROLE_USER");
    }

    @Test
    void transfer_success() throws Exception {
        String requestBody = """
                {
                    "fromCard": "1234567812345678",
                    "toCard": "8765432187654321",
                    "amount": 100.50
                }
                """;

        mockMvc.perform(patch("/transactions/transfers")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, null)
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(transactionsService, times(1))
                .transfer(any(TransactionRequest.class), eq(user.getUserId()));
    }

    @Test
    void transfer_invalidAmount() throws Exception {
        String requestBody = """
                {
                    "fromCard": "1234567812345678",
                    "toCard": "8765432187654321",
                    "amount": 0.0
                }
                """;

        doThrow(new InvalidCurrencyAmount("Amount must be greater than or equal to 0.01"))
                .when(transactionsService).transfer(any(TransactionRequest.class), eq(user.getUserId()));

        mockMvc.perform(patch("/transactions/transfers")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, null)
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
