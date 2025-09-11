package com.example.bankcards.service;

import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import com.example.bankcards.exception.InvalidCredentialsException;
import com.example.bankcards.exception.UserExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.RefreshTokensRepository;
import com.example.bankcards.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UsersRepository usersRepository;
    private PasswordEncoder passwordEncoder;
    private RefreshTokensRepository refreshTokensRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        usersRepository = mock(UsersRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        refreshTokensRepository = mock(RefreshTokensRepository.class);

        authService = new AuthService(usersRepository, passwordEncoder, refreshTokensRepository);
    }

    @Test
    void register_success() {
        User user = new User();
        user.setUsername("new_user");
        user.setPassword("password");

        when(usersRepository.existsByUsername("new_user")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        authService.register(user);

        assertEquals(Role.USER, user.getRole());
        assertEquals("encodedPassword", user.getPassword());
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    void register_userExists_throwsException() {
        User user = new User();
        user.setUsername("existing_user");

        when(usersRepository.existsByUsername("existing_user")).thenReturn(true);

        assertThrows(UserExistsException.class, () -> authService.register(user));
        verify(usersRepository, never()).save(any());
    }

    @Test
    void checkAndGet_success() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");

        User userFromDb = new User();
        userFromDb.setUsername("user");
        userFromDb.setPassword("encodedPassword");

        when(usersRepository.findByUsername("user")).thenReturn(Optional.of(userFromDb));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        User result = authService.checkAndGet(user);

        assertEquals(userFromDb, result);
    }

    @Test
    void checkAndGet_wrongPassword_throwsException() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("wrong");

        User userFromDb = new User();
        userFromDb.setUsername("user");
        userFromDb.setPassword("encodedPassword");

        when(usersRepository.findByUsername("user")).thenReturn(Optional.of(userFromDb));
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.checkAndGet(user));
    }

    @Test
    void checkAndGet_userNotFound_throwsException() {
        User user = new User();
        user.setUsername("user");

        when(usersRepository.findByUsername("user")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.checkAndGet(user));
    }

    @Test
    void updateRefreshToken_existingToken_updatesToken() {
        Long userId = 1L;
        RefreshToken existing = new RefreshToken(userId, "oldToken");
        when(refreshTokensRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        authService.updateRefreshToken(userId, "newToken");

        assertEquals("newToken", existing.getRefreshToken());
        verify(refreshTokensRepository, never()).save(any());
    }

    @Test
    void updateRefreshToken_noToken_savesToken() {
        Long userId = 1L;
        when(refreshTokensRepository.findByUserId(userId)).thenReturn(Optional.empty());

        authService.updateRefreshToken(userId, "newToken");

        verify(refreshTokensRepository, times(1)).save(any());
    }
}
