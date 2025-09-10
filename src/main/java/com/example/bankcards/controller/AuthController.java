package com.example.bankcards.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.bankcards.dto.AuthRequest;
import com.example.bankcards.dto.JWTResponse;
import com.example.bankcards.dto.RefreshTokenRequest;
import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidRefreshTokenException;
import com.example.bankcards.exception.LoginException;
import com.example.bankcards.exception.RegistrationException;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.TokenService;
import com.example.bankcards.service.AdminUsersService;
import com.example.bankcards.util.BankModelMapper;
import com.example.bankcards.util.RequestValidator;
import com.example.bankcards.util.UserValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
@Tag(name = "Authentication Controller", description = "APIs to authenticate to the system")
public class AuthController {

    private final BankModelMapper bankModelMapper;
    private final UserValidator userValidator;
    private final AuthService authService;
    private final TokenService tokenService;
    private final AdminUsersService adminUsersService;

    public AuthController(BankModelMapper bankModelMapper, UserValidator userValidator, AuthService authService, TokenService tokenService, AdminUsersService adminUsersService) {
        this.bankModelMapper = bankModelMapper;
        this.userValidator = userValidator;
        this.authService = authService;
        this.tokenService = tokenService;
        this.adminUsersService = adminUsersService;
    }

    @Operation(summary = "Register in the system", description = "You can register with username and password")
    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(
            @RequestBody @Valid AuthRequest authRequest,
            BindingResult bindingResult
    ) {
        User newUser = bankModelMapper.convertToUser(authRequest);
        // Check if there is already user with such username in DB
        userValidator.validate(newUser, bindingResult);

        // If there are some validation errors after mapping to user
        // then throw RegisterException
        RequestValidator.validateRequest(bindingResult, RegistrationException::new);
        authService.register(newUser);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(summary = "Login to the system", description = "You can login to the system with username and password")
    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(
            @RequestBody @Valid AuthRequest authDTO,
            BindingResult bindingResult
    ) {
        User user = bankModelMapper.convertToUser(authDTO);
        // If there are some validation errors after mapping to user
        // then throw LoginException
        RequestValidator.validateRequest(bindingResult, LoginException::new);

        // If such a person exists in DB
        User userDB = authService.checkAndGet(user);

        // Generating tokens
        String accessToken = tokenService.generateAccessToken(userDB);
        String refreshToken = tokenService.generateRefreshToken(userDB);

        // Update refresh token
        authService.updateRefreshToken(userDB.getId(), refreshToken);

        return new ResponseEntity<>(new JWTResponse(accessToken, refreshToken), HttpStatus.OK);
    }

    @Operation(summary = "Refresh tokens", description = "Getting new pair of tokens with current refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<JWTResponse> refreshToken(
            @RequestBody @Valid RefreshTokenRequest refreshTokenRequest
    ) {
        int userId;

        try {
            //Checking if refresh token is valid
            DecodedJWT jwt = tokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
            userId = jwt.getClaim("id").asInt();

            // Getting current user's refresh token in order to compare
            RefreshToken refreshToken = authService.findRefreshToken(userId);
            if (refreshToken == null || !refreshToken.getRefreshToken().equals(refreshTokenRequest.getRefreshToken())) {
                throw new InvalidRefreshTokenException("Invalid refresh token");
            }
        } catch (JWTVerificationException e) {
            // If jwt refresh token is invalid, then return nothing with this message
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        // Getting person by id in order to generate new tokens
        User user = adminUsersService.findOne(userId);

        // Generating a new pair of tokens
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        // Update current refresh token on new refresh token
        authService.updateRefreshToken(userId, refreshToken);

        return new ResponseEntity<>(new JWTResponse(accessToken, refreshToken), HttpStatus.OK);
    }

}
