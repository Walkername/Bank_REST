package com.example.bankcards.controller;

import com.example.bankcards.dto.CardNumberResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.CardsService;
import com.example.bankcards.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CardsController.class)
class CardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardsService cardsService;

    @MockitoBean
    TokenService tokenService;

    private UserPrincipal user;

    @BeforeEach
    void setUp() {
        user = new UserPrincipal(1L, "test_user", "USER");
    }

    @Test
    void getCard_success() throws Exception {
        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(1L);
        cardResponse.setOwnerId(user.getUserId());
        cardResponse.setCardNumber("**** **** **** 1234");

        when(cardsService.findOne(1L, user.getUserId())).thenReturn(cardResponse);

        mockMvc.perform(get("/cards/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, null)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ownerId").value(user.getUserId()))
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 1234"));
    }

    @Test
    void getBalance_success() throws Exception {
        when(cardsService.getBalance(1L, user.getUserId())).thenReturn(BigDecimal.valueOf(1500.75));

        mockMvc.perform(get("/cards/1/balance")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, null)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1500.75));
    }

    @Test
    void getCardNumber_success() throws Exception {
        CardNumberResponse numberResponse = new CardNumberResponse("1234567812345678");
        when(cardsService.getCardNumber(1L, user.getUserId())).thenReturn(numberResponse);

        mockMvc.perform(get("/cards/1/card-number")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, null)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("1234567812345678"));
    }

    @Test
    void requestCardBlocking_success() throws Exception {
        mockMvc.perform(patch("/cards/block/1")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, null)
                        ))
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void getCards_success() throws Exception {
        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(1L);
        cardResponse.setOwnerId(user.getUserId());
        cardResponse.setCardNumber("**** **** **** 1234");

        PageResponse<CardResponse> pageResponse = new PageResponse<>(
                List.of(cardResponse),
                0,
                10,
                1,
                1
        );

        when(cardsService.getUserCards(user.getUserId(), 0, 10, new String[]{"balance:desc"}, null, null, null))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/cards/cards")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(user, null, null)
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].ownerId").value(user.getUserId()))
                .andExpect(jsonPath("$.content[0].cardNumber").value("**** **** **** 1234"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.limit").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}
