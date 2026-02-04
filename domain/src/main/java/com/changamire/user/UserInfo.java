package com.changamire.user;

import com.changamire.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * User Information DTO
 * 
 * Data Transfer Object containing user profile information.
 * Used in authentication responses and user profile endpoints.
 * Excludes sensitive data like passwords.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Builder
@Schema(description = "User profile information")
public record UserInfo(
    
    @Schema(description = "User ID", example = "1")
    Long id,
    
    @Schema(description = "Username", example = "john_doe")
    String username,
    
    @Schema(description = "Email address", example = "john@example.com")
    String email,
    
    @Schema(description = "First name", example = "John")
    String firstName,
    
    @Schema(description = "Last name", example = "Doe")
    String lastName,
    
    @Schema(description = "User role", example = "CUSTOMER")
    UserRole role
) {}
