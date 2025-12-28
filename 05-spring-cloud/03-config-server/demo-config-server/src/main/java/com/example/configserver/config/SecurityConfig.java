package com.example.configserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration for Config Server
 * 
 * Secures Config Server endpoints with Basic Authentication.
 * In production, consider:
 * - HTTPS/TLS encryption
 * - OAuth2 authentication
 * - Integration with identity providers
 * - Role-based access control
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST API
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization
            .authorizeHttpRequests(auth -> auth
                // Allow health check without authentication
                .requestMatchers("/actuator/health").permitAll()
                
                // Require authentication for all other endpoints
                .anyRequest().authenticated()
            )
            
            // Enable HTTP Basic authentication
            .httpBasic(Customizer.withDefaults());
        
        return http.build();
    }

    /**
     * Configure in-memory user details
     * 
     * For production:
     * - Use JDBC or LDAP UserDetailsService
     * - Store credentials securely
     * - Implement password policies
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("configuser")
            .password(passwordEncoder().encode("configpass"))
            .roles("USER")
            .build();
        
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("USER", "ADMIN")
            .build();
        
        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * Password encoder for secure password storage
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
