package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.TokenService;
import com.example.bankcards.service.AdminUsersService;
import com.example.bankcards.util.BankModelMapper;
import com.example.bankcards.util.UserValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankModelMapper bankModelMapper;

    @MockitoBean
    private UserValidator userValidator;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private AdminUsersService adminUsersService;

    @Test
    void register_success() throws Exception {
        String jsonRequest = "{\"username\":\"new_user\",\"password\":\"password\"}";

        User user = new User();
        user.setUsername("new_user");
        user.setPassword("password");

        when(bankModelMapper.convertToUser(any(AuthRequest.class))).thenReturn(user);

        doNothing().when(userValidator).validate(any(User.class), any());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(authService, times(1)).register(user);
    }

    @Test
    void login_success() throws Exception {
        String jsonRequest = "{\"username\":\"existing_user\",\"password\":\"password\"}";

        User user = new User();
        user.setId(1L);
        user.setUsername("existing_user");
        user.setPassword("password");

        when(bankModelMapper.convertToUser(any(AuthRequest.class))).thenReturn(user);
        when(authService.checkAndGet(user)).thenReturn(user);
        when(tokenService.generateAccessToken(user)).thenReturn("access-token");
        when(tokenService.generateRefreshToken(user)).thenReturn("refresh-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(authService, times(1)).checkAndGet(user);
        verify(tokenService, times(1)).generateAccessToken(user);
        verify(tokenService, times(1)).generateRefreshToken(user);
        verify(authService, times(1)).updateRefreshToken(user.getId(), "refresh-token");
    }
}
