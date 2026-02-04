package com.changamire.configs;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Auditor Aware Implementation
 * 
 * Provides the current user for JPA auditing. Returns "system" as default
 * when no authenticated user is available. Can be enhanced with Spring
 * Security to return actual logged-in usernames.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // TODO: Integrate with Spring Security when authentication is added
        // Example: return Optional.ofNullable(SecurityContextHolder.getContext()
        //                  .getAuthentication().getName());
        
        // For now, return "system" as default
        return Optional.of("system");
    }
}
