package com.shangmin.whisperrr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// Spring Security imports removed for testing - will add back when implementing authentication

import java.util.Optional;

/**
 * JPA configuration class for enabling auditing and repository scanning.
 * 
 * @author shangmin
 * @version 1.0
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.shangmin.whisperrr.repository")
@EnableJpaAuditing
public class JpaConfig {
    
    /**
     * Provides the current auditor (user) for JPA auditing.
     * This implementation uses Spring Security to get the current user.
     * 
     * @return AuditorAware<String> the auditor aware implementation
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }
    
    /**
     * Implementation of AuditorAware that provides a default auditor.
     * This is a simplified version for testing - in production, this would
     * integrate with Spring Security to get the current authenticated user.
     */
    public static class SpringSecurityAuditorAware implements AuditorAware<String> {
        
        /**
         * Gets the current auditor (username) for auditing purposes.
         * For testing, we'll return a default value.
         * 
         * @return Optional<String> the current username
         */
        @Override
        public Optional<String> getCurrentAuditor() {
            // For testing purposes, return a default auditor
            // In production, this would integrate with Spring Security
            return Optional.of("system");
        }
    }
}
