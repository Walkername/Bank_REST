package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.ChangeUsernameRequest;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.AdminCardsService;
import com.example.bankcards.service.AdminUsersService;
import com.example.bankcards.service.CardsService;
import com.example.bankcards.service.TokenService;
import com.example.bankcards.util.BankModelMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminUsersController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class AdminUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    AdminCardsService adminCardsService;

    @MockitoBean
    TokenService tokenService;

    @MockitoBean
    private AdminUsersService adminUsersService;

    @MockitoBean
    private CardsService cardsService;

    @MockitoBean
    private BankModelMapper bankModelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserResponse();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
    }

    @Test
    void getUser_success() throws Exception {
        Mockito.when(adminUsersService.findOne(1L)).thenReturn(Mockito.mock(User.class));
        Mockito.when(bankModelMapper.convertToUserResponse(any())).thenReturn(testUser);

        mockMvc.perform(get("/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_doe"));
    }

    @Test
    void assignAdminRole_success() throws Exception {
        mockMvc.perform(patch("/admin/users/1/role"))
                .andExpect(status().isOk());

        Mockito.verify(adminUsersService).assignAdminRole(1L);
    }

    @Test
    void changeUsername_success() throws Exception {
        ChangeUsernameRequest request = new ChangeUsernameRequest();
        request.setUsername("new_username");

        mockMvc.perform(patch("/admin/users/1/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(adminUsersService).changeUsername(1L, "new_username");
    }

    @Test
    void getUserCards_success() throws Exception {
        CardResponse cardResponse = new CardResponse();
        cardResponse.setId(1L);
        PageResponse<CardResponse> pageResponse = new PageResponse<>(
                Collections.singletonList(cardResponse), 0, 10, 1, 1
        );

        Mockito.when(cardsService.getUserCards(anyLong(), anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/admin/users/1/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }
}
