package com.changamire.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Signup Request DTO
 * <p>
 * Data Transfer Object for user registration/signup requests.
 * Contains validation rules for creating a new user account.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Builder
@Schema(description = "User registration request")
public record SignupRequest(
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Username for the account", example = "john_doe")
    String username,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User email address", example = "john@example.com")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "Account password (minimum 6 characters)", example = "SecurePass123!")
    String password,
    
    @NotBlank(message = "First name is required")
    @Schema(description = "User's first name", example = "John")
    String firstName,
    
    @NotBlank(message = "Last name is required")
    @Schema(description = "User's last name", example = "Doe")
    String lastName,
    
    @Schema(description = "User's phone number", example = "0771234567")
    String phoneNumber
) {}
