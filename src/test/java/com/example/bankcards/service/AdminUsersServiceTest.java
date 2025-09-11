package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import com.example.bankcards.exception.UserExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminUsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    private AdminUsersService adminUsersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminUsersService = new AdminUsersService(usersRepository);
    }

    @Test
    void findOne_userExists() {
        User user = new User();
        user.setId(1L);
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = adminUsersService.findOne(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findOne_userNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminUsersService.findOne(1L));
    }

    @Test
    void changeUsername_success() {
        User user = new User();
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.existsByUsername("new_username")).thenReturn(false);

        adminUsersService.changeUsername(1L, "new_username");

        assertEquals("new_username", user.getUsername());
    }

    @Test
    void changeUsername_userNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminUsersService.changeUsername(1L, "new_username"));
    }

    @Test
    void changeUsername_userExistsException() {
        User user = new User();
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(usersRepository.existsByUsername("taken")).thenReturn(true);

        assertThrows(UserExistsException.class, () -> adminUsersService.changeUsername(1L, "taken"));
    }

    @Test
    void assignAdminRole_success() {
        User user = new User();
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        adminUsersService.assignAdminRole(1L);

        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void assignAdminRole_userNotFound() {
        when(usersRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminUsersService.assignAdminRole(1L));
    }
}
