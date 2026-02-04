package com.changamire.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Token Provider
 * 
 * Utility class for generating and validating JWT tokens. Handles both
 * access tokens (24 hours) and refresh tokens (7 days). Uses HMAC-SHA
 * algorithm for token signing and verification.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;
    
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpirationMs;
    
    /**
     * Generate JWT access token from authentication
     * @param authentication Spring Security authentication object
     * @return JWT access token string
     */
    public String generateAccessToken(Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();
        var now = new Date();
        var expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Generate JWT refresh token
     * @param username the username to generate token for
     * @return JWT refresh token string
     */
    public String generateRefreshToken(String username) {
        var now = new Date();
        var expiryDate = new Date(now.getTime() + refreshExpirationMs);
        
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Extract username from JWT token
     * @param token JWT token string
     * @return username contained in the token
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    
    /**
     * Validate JWT token
     * @param token JWT token string to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Log the exception if needed
            return false;
        }
    }
    
    /**
     * Get the signing key for JWT operations
     * @return SecretKey for signing and verifying tokens
     */
    private SecretKey getSigningKey() {
        var keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
