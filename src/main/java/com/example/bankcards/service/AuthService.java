package com.example.bankcards.service;

import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.RefreshTokensRepository;
import com.example.bankcards.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokensRepository refreshTokensRepository;

    @Autowired
    public AuthService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, RefreshTokensRepository refreshTokensRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokensRepository = refreshTokensRepository;
    }

    @Transactional
    public void register(User user) {
        if (usersRepository.existsById(user.getId())) {
            throw new UserExistsException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        usersRepository.save(user);
    }

    public User checkAndGet(User user) {
        Optional<User> optionalUser = usersRepository.findByUsername(user.getUsername());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        User userFound = optionalUser.get();

        if (!passwordEncoder.matches(user.getPassword(), userFound.getPassword())) {
            throw new InvalidCredentialsException("Wrong credentials");
        }
        return userFound;
    }

    public RefreshToken findRefreshToken(int userId) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokensRepository.findByUserId(userId);
        return refreshTokenOptional.orElse(null);
    }

    @Transactional
    public void updateRefreshToken(int userId, String newRefreshToken) {
        Optional<RefreshToken> refreshToken = refreshTokensRepository.findByUserId(userId);
        // If DB does not store refresh token for this user, then
        // it just will be saved in DB
        if (refreshToken.isPresent()) {
            refreshToken.get().setRefreshToken(newRefreshToken);
        } else {
            refreshTokensRepository.save(new RefreshToken(userId, newRefreshToken));
        }
    }

}
