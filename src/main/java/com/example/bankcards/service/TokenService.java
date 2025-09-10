package com.example.bankcards.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.bankcards.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;

@Service
public class TokenService {

    @Value("${spring.auth.jwt.access}")
    private String accessToken;

    @Value("${spring.auth.jwt.refresh}")
    private String refreshToken;

    public String generateAccessToken(User user) {
        Date expiresAt = Date.from(ZonedDateTime.now().plusMinutes(15).toInstant());

        return JWT.create()
                .withSubject("Bank details")
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole().toString())
                .withIssuedAt(new Date())
                .withIssuer("bank-service")
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(accessToken));
    }

    public String generateRefreshToken(User user) {
        Date expiresAt = Date.from(ZonedDateTime.now().plusDays(30).toInstant());

        return JWT.create()
                .withSubject("Bank details")
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("role", user.getRole().toString())
                .withIssuedAt(new Date())
                .withIssuer("bank-service")
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(refreshToken));
    }

    public DecodedJWT validateAccessToken(String token) {
        return validateToken(token, accessToken);
    }

    public DecodedJWT validateRefreshToken(String token) {
        return validateToken(token, refreshToken);
    }

    public DecodedJWT validateToken(String token, String secret) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("Bank details")
                .withIssuer("bank-service")
                .build();

        return verifier.verify(token);
    }

}
