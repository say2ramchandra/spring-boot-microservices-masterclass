package com.example.gateway.controller;

import com.example.gateway.dto.LoginRequest;
import com.example.gateway.dto.LoginResponse;
import com.example.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * 
 * Handles login and token generation.
 * In production, delegate to a dedicated Auth Service.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Login endpoint - generates JWT token
     * 
     * Test with:
     * curl -X POST http://localhost:8080/auth/login \
     *   -H "Content-Type: application/json" \
     *   -d '{"username":"admin","password":"password"}'
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("🔐 Login attempt for user: {}", request.getUsername());
        
        // Simple validation (in production, check against database)
        if ("admin".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            String token = jwtUtil.generateToken(request.getUsername());
            
            log.info("✅ Login successful for user: {}", request.getUsername());
            
            return ResponseEntity.ok(new LoginResponse(
                    token,
                    "Bearer",
                    3600L, // 1 hour
                    request.getUsername()
            ));
        }
        
        log.warn("❌ Login failed for user: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Validate token endpoint
     */
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            jwtUtil.validateToken(token);
            String username = jwtUtil.extractUsername(token);
            
            return ResponseEntity.ok("Token is valid for user: " + username);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token: " + e.getMessage());
        }
    }
}
