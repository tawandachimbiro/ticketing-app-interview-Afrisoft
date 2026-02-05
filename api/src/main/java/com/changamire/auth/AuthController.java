package com.changamire.auth;

import com.changamire.security.AuthService;
import com.changamire.user.JwtAuthResponse;
import com.changamire.user.LoginRequest;
import com.changamire.user.SignupRequest;
import com.changamire.user.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * <p>
 * REST controller for user authentication operations. Handles user signup,
 * login, and profile retrieval. Returns JWT tokens for successful authentication.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User signup, login, and authentication operations")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Register a new user
     * @param request signup request with user details
     * @return JWT authentication response with access and refresh tokens
     */
    @PostMapping("/signup")
    @Operation(summary = "Register new user", 
               description = "Create a new user account and receive JWT access & refresh tokens. Default role is CUSTOMER.")
    public ResponseEntity<JwtAuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        var response = authService.signup(request);
        return ResponseEntity.status(response.success() ? 200 : 400).body(response);
    }
    
    /**
     * Authenticate user and generate tokens
     * @param request login request with credentials
     * @return JWT authentication response with access and refresh tokens
     */
    @PostMapping("/login")
    @Operation(summary = "User login", 
               description = "Authenticate user with username and password. Returns JWT access token (24h) and refresh token (7d).")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var response = authService.login(request);
        return ResponseEntity.status(response.success() ? 200 : 401).body(response);
    }
    
    /**
     * Get current authenticated user details
     * @return user profile information
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user", 
               description = "Get authenticated user profile details. Requires valid JWT token in Authorization header.")
    public ResponseEntity<UserInfo> getCurrentUser() {
        var user = authService.getCurrentUser();
        var userInfo = new UserInfo(
            user.getId(), user.getUsername(), user.getEmail(),
            user.getFirstName(), user.getLastName(), user.getRole()
        );
        return ResponseEntity.ok(userInfo);
    }
}
