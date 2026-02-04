package com.changamire.configs;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Auditor Aware Implementation
 * 
 * Provides the current authenticated user for JPA auditing. Integrates with
 * Spring Security to extract the username from JWT token. Falls back to
 * "system" when no authenticated user is available.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .filter(auth -> !"anonymousUser".equals(auth.getPrincipal()))
                .map(Authentication::getName)
                .or(() -> Optional.of("system"));
    }
}
