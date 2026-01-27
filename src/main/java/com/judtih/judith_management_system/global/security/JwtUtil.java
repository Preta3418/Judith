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


    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("userId", Long.class);
    }


    public String getStudentNumberFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }


    public boolean getIsAdminFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("isAdmin", Boolean.class);
    }


    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
