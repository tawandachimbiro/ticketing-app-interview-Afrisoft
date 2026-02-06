package com.changamire.security;

import com.changamire.enums.UserRole;
import com.changamire.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication Service
 * 
 * Handles user authentication operations including signup, login, and
 * current user retrieval. Manages JWT token generation and user validation.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * Register a new user
     * @param request signup request containing user details
     * @return JWT authentication response with tokens
     */
    public JwtAuthResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return new JwtAuthResponse(false, "Username already exists", null, null, null, null);
        }

        if (userRepository.existsByEmail(request.email())) {
            return new JwtAuthResponse(false, "Email already exists", null, null, null, null);
        }

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .role(UserRole.CUSTOMER)
                .enabled(true)
                .accountNonLocked(true)
                .build();
        
        userRepository.save(user);
        
        // Generate tokens
        var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        
        var accessToken = jwtTokenProvider.generateAccessToken(authentication);
        var refreshToken = jwtTokenProvider.generateRefreshToken(request.username());
        
        var userInfo = new UserInfo(
            user.getId(), user.getUsername(), user.getEmail(),
            user.getFirstName(), user.getLastName(), user.getRole()
        );
        
        return new JwtAuthResponse(true, "User registered successfully", 
            accessToken, refreshToken, 86400L, userInfo);
    }
    
    /**
     * Authenticate user and generate tokens
     * @param request login request containing credentials
     * @return JWT authentication response with tokens
     */
    public JwtAuthResponse login(LoginRequest request) {
        try {
            var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            var user = userRepository.findByUsername(request.username())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
            var accessToken = jwtTokenProvider.generateAccessToken(authentication);
            var refreshToken = jwtTokenProvider.generateRefreshToken(request.username());
            
            var userInfo = new UserInfo(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(), user.getRole()
            );
            
            return new JwtAuthResponse(true, "Login successful", 
                accessToken, refreshToken, 86400L, userInfo);
                
        } catch (AuthenticationException e) {
            return new JwtAuthResponse(false, "Invalid username or password", 
                null, null, null, null);
        }
    }
    
    /**
     * Get the currently authenticated user
     * @return User entity of the authenticated user
     * @throws UsernameNotFoundException if user is not found
     */
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
