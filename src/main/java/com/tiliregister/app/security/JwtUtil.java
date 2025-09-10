package com.tiliregister.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate access token with userId as subject
    public String generateAccessToken(Long userId, String username) {
        return generateToken(userId, username, ACCESS_TOKEN_EXPIRATION);
    }

    // Generate refresh token with userId as subject
    public String generateRefreshToken(Long userId, String username) {
        return generateToken(userId, username, REFRESH_TOKEN_EXPIRATION);
    }

    private String generateToken(Long userId, String username, long durationMillis) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))   // subject = userId
                .claim("username", username)          // optional claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + durationMillis))
                .signWith(key)
                .compact();
    }

    // Extract userId (from subject)
    public Long extractUserId(String token) {
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }

    // Extract username (from claims)
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("username", String.class);
    }

    // Validate token (signature + expiration)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}