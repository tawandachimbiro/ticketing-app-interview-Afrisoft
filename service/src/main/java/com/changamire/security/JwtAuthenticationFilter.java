package com.changamire.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 * 
 * Servlet filter that intercepts HTTP requests to extract and validate JWT tokens.
 * If a valid token is found in the Authorization header, it authenticates the user
 * and sets the security context. Runs once per request.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    
    /**
     * Filter method to extract and validate JWT token from requests
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain filter chain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            var token = extractTokenFromRequest(request);
            
            if (token != null && jwtTokenProvider.validateToken(token)) {
                var username = jwtTokenProvider.getUsernameFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(username);
                
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Log error if needed, but don't block the request
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract JWT token from Authorization header
     * @param request HTTP request
     * @return JWT token string or null if not found
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        var bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
