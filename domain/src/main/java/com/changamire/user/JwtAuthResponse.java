package com.changamire.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * JWT Authentication Response DTO
 * 
 * Data Transfer Object for authentication responses containing JWT tokens.
 * Returned after successful signup or login operations.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Builder
@Schema(description = "JWT authentication response")
public record JwtAuthResponse(
    
    @Schema(description = "Whether the operation was successful", example = "true")
    boolean success,
    
    @Schema(description = "Response message", example = "Login successful")
    String message,
    
    @Schema(description = "JWT access token (valid for 24 hours)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,
    
    @Schema(description = "JWT refresh token (valid for 7 days)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String refreshToken,
    
    @Schema(description = "Token type", example = "Bearer")
    String tokenType,
    
    @Schema(description = "Token expiration time in seconds", example = "86400")
    Long expiresIn,
    
    @Schema(description = "Authenticated user details")
    UserInfo user
) {
    public JwtAuthResponse(boolean success, String message, String accessToken, String refreshToken, Long expiresIn, UserInfo user) {
        this(success, message, accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
