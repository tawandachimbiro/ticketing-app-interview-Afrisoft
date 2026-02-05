package com.changamire.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Login Request DTO
 * <p>
 * Data Transfer Object for user login/authentication requests.
 * Contains user credentials for authentication.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Builder
@Schema(description = "User login request")
public record LoginRequest(
    
    @NotBlank(message = "Username is required")
    @Schema(description = "Username or email", example = "john_doe")
    String username,
    
    @NotBlank(message = "Password is required")
    @Schema(description = "Account password", example = "SecurePass123!")
    String password
) {}
