package com.paulaccesso.visitor.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.paulaccesso.visitor.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public String generateAccessToken(User user) {
        return JWT.create()
                .withSubject(user.getEmpId())
                .withClaim("userId", user.getId())
                .withClaim("empId", user.getEmpId())
                .withClaim("role", user.getRole().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plus(expiration, ChronoUnit.MILLIS)))
                .sign(Algorithm.HMAC256(secret));
    }

    public String generateRefreshToken(User user) {
        return JWT.create()
                .withSubject(user.getEmpId())
                .withClaim("userId", user.getId())
                .withIssuedAt(new Date())
                .withExpiresAt(Date.from(Instant.now().plus(refreshExpiration, ChronoUnit.MILLIS)))
                .sign(Algorithm.HMAC256(secret));
    }

    public String getEmpIdFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
        return decodedJWT.getSubject();
    }

    public Long getUserIdFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
        return decodedJWT.getClaim("userId").asLong();
    }

    public String getRoleFromToken(String token) {
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret))
                .build()
                .verify(token);
        return decodedJWT.getClaim("role").asString();
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}