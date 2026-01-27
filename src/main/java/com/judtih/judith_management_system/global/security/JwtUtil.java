package com.judtih.judith_management_system.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        // Create a secure key from the secret string
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }


    public String generateToken(Long userId, String studentNumber, boolean isAdmin) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(studentNumber)
                .claim("userId", userId)
                .claim("isAdmin", isAdmin)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();

    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Extract userId from token
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("userId", Long.class);
    }

    // Extract studentNumber from token
    public String getStudentNumberFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    // Extract isAdmin from token
    public boolean getIsAdminFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("isAdmin", Boolean.class);
    }

    // Helper to parse claims
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
