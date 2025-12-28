package com.example.gateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Utility Class
 * 
 * Handles JWT token generation and validation.
 */
@Slf4j
@Component
public class JwtUtil {

    // In production, store this securely (environment variable, vault, etc.)
    private static final String SECRET_KEY = "MySecretKeyForJWTTokenGenerationAndValidationMustBeLongEnough";
    private static final long EXPIRATION_TIME = 3600000; // 1 hour

    private final SecretKey key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Generate JWT token for a username
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Validate token
     */
    public void validateToken(String token) {
        try {
            extractClaims(token);
            log.debug("✅ Token validation successful");
        } catch (ExpiredJwtException e) {
            log.error("❌ Token has expired");
            throw new RuntimeException("Token has expired", e);
        } catch (UnsupportedJwtException e) {
            log.error("❌ Unsupported JWT token");
            throw new RuntimeException("Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            log.error("❌ Malformed JWT token");
            throw new RuntimeException("Malformed JWT token", e);
        } catch (SignatureException e) {
            log.error("❌ Invalid JWT signature");
            throw new RuntimeException("Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            log.error("❌ JWT claims string is empty");
            throw new RuntimeException("JWT claims string is empty", e);
        }
    }

    /**
     * Extract all claims from token
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
