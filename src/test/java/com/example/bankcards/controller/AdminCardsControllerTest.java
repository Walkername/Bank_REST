package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.ChangeCardStatusRequest;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.service.AdminCardsService;
import com.example.bankcards.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminCardsController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class AdminCardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    TokenService tokenService;

    @MockitoBean
    private AdminCardsService adminCardsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCard_success() throws Exception {
        CardResponse response = new CardResponse();
        response.setId(1L);
        response.setOwnerId(2L);
        response.setCardNumber("**** **** **** 5678");
        response.setBalance(BigDecimal.valueOf(174.88));
        response.setStatus(CardStatus.ACTIVE);

        Mockito.when(adminCardsService.findOne(1L)).thenReturn(response);

        mockMvc.perform(get("/admin/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ownerId").value(2L))
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 5678"));
    }

    @Test
    void createCard_success() throws Exception {
        mockMvc.perform(post("/admin/cards/create/5"))
                .andExpect(status().isCreated());
        Mockito.verify(adminCardsService).create(5L);
    }

    @Test
    void changeStatus_success() throws Exception {
        ChangeCardStatusRequest request = new ChangeCardStatusRequest();
        request.setStatus("ACTIVE");

        mockMvc.perform(patch("/admin/cards/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(adminCardsService).setStatus(1L, CardStatus.ACTIVE);
    }

    @Test
    void changeStatus_wrongStatus() throws Exception {
        ChangeCardStatusRequest request = new ChangeCardStatusRequest();
        request.setStatus("EXPIRED");

        mockMvc.perform(patch("/admin/cards/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCard_success() throws Exception {
        mockMvc.perform(delete("/admin/cards/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(adminCardsService).deleteCard(1L);
    }
}