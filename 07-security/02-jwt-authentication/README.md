# Section 02: JWT Authentication

## Overview

JWT (JSON Web Token) is a compact, URL-safe means of representing claims between two parties. This section covers implementing stateless authentication with JWT in Spring Boot microservices.

## Table of Contents
1. [What is JWT](#what-is-jwt)
2. [JWT Structure](#jwt-structure)
3. [JWT vs Session Authentication](#jwt-vs-session-authentication)
4. [Implementation](#implementation)
5. [JWT Security](#jwt-security)
6. [Best Practices](#best-practices)
7. [Working Demo](#working-demo)
8. [Interview Questions](#interview-questions)

---

## What is JWT

**JWT (JSON Web Token)** is an open standard (RFC 7519) for securely transmitting information between parties as a JSON object.

### Key Characteristics

- **Compact**: Can be sent via URL, POST parameter, or HTTP header
- **Self-contained**: Contains all necessary information about the user
- **Stateless**: Server doesn't need to store session information
- **Secure**: Digitally signed to verify authenticity

### Use Cases

✅ **Authentication**: User logs in → receives JWT → uses JWT for subsequent requests  
✅ **Information Exchange**: Securely transmit data between parties  
✅ **Single Sign-On (SSO)**: Share authentication across multiple services  
✅ **API Authorization**: Secure REST APIs without sessions  

---

## JWT Structure

A JWT consists of three parts separated by dots (`.`):

```
Header.Payload.Signature
```

### Complete Example

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJqb2huLmRvZSIsImlhdCI6MTY4ODEyMzQ1NiwiZXhwIjoxNjg4MTI3MDU2LCJyb2xlcyI6WyJVU0VSIiwiQURNSU4iXX0.
4Adl-II8H8nSYOx3Nl2Y8DqZ9ux_3H72p-W2F0QGt9M
```

### 1. Header

Contains metadata about the token:

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

- `alg`: Signing algorithm (HS256, RS256, etc.)
- `typ`: Token type (JWT)

### 2. Payload

Contains the claims (user data):

```json
{
  "sub": "john.doe",
  "name": "John Doe",
  "email": "john@example.com",
  "iat": 1688123456,
  "exp": 1688127056,
  "roles": ["USER", "ADMIN"]
}
```

**Standard Claims:**
- `sub`: Subject (user ID)
- `iat`: Issued At (timestamp)
- `exp`: Expiration Time (timestamp)
- `iss`: Issuer
- `aud`: Audience

**Custom Claims:**
- `roles`: User roles
- `permissions`: User permissions
- Any application-specific data

### 3. Signature

Ensures token hasn't been tampered with:

```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

---

## JWT vs Session Authentication

| Aspect | JWT | Session |
|--------|-----|---------|
| **Storage** | Client-side (localStorage/cookie) | Server-side (memory/database) |
| **Stateless** | ✅ Yes | ❌ No |
| **Scalability** | ✅ Easy (no shared state) | ⚠️ Harder (session replication needed) |
| **Size** | ⚠️ Larger (1-2KB) | ✅ Smaller (session ID only) |
| **Revocation** | ⚠️ Difficult | ✅ Easy |
| **Security** | ⚠️ XSS vulnerable if in localStorage | ✅ CSRF protection needed |
| **Performance** | ✅ Fast (no DB lookup) | ⚠️ DB lookup per request |
| **Best For** | Microservices, APIs | Monolithic apps |

---

## Implementation

### Dependencies

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT Library -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### Configuration

```yaml
application:
  security:
    jwt:
      secret-key: ${JWT_SECRET:mySecretKeyMustBeLongEnoughForHS512AlgorithmAtLeast512Bits}
      expiration: 86400000  # 24 hours in milliseconds
      refresh-token-expiration: 604800000  # 7 days
```

### JWT Utility Class

```java
@Component
public class JwtTokenProvider {
    
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshExpiration;
    
    private SecretKey key;
    
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }
    
    /**
     * Generate JWT token from UserDetails
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
        
        return buildToken(claims, userDetails.getUsername(), jwtExpiration);
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails.getUsername(), refreshExpiration);
    }
    
    /**
     * Build JWT token
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            String username,
            long expiration) {
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract specific claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Validate token
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Validate token (without UserDetails)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
```

### JWT Authentication Filter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Extract JWT token from request
            String token = extractTokenFromRequest(request);
            
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // Extract username from token
                String username = jwtTokenProvider.extractUsername(token);
                
                // Load user details
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Create authentication object
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                
                // Set authentication details
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("Set authentication for user: {}", username);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
    
    /**
     * Skip JWT filter for public endpoints
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || 
               path.startsWith("/api/public/") ||
               path.equals("/error");
    }
}
```

### Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/api/public/**",
                    "/h2-console/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
            );
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Authentication Controller

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    
    /**
     * User registration
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request) {
        
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @RequestBody RefreshTokenRequest request) {
        
        AuthenticationResponse response = authenticationService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Logout (optional - client removes token)
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // In stateless JWT, logout is typically handled client-side
        // Optionally implement token blacklist here
        return ResponseEntity.ok().build();
    }
}

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8)
    private String password;
    
    private String firstName;
    private String lastName;
}

@Data
public class LoginRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private String username;
    private List<String> roles;
}

@Data
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}
```

### Authentication Service

```java
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Register new user
     */
    public AuthenticationResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Create user
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .enabled(true)
            .roles(Set.of(Role.builder().name("ROLE_USER").build()))
            .build();
        
        userRepository.save(user);
        
        // Generate tokens
        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        
        return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .username(user.getUsername())
            .roles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()))
            .build();
    }
    
    /**
     * Authenticate user and generate tokens
     */
    public AuthenticationResponse login(LoginRequest request) {
        // Authenticate
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        // Load user
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Generate tokens
        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
        
        return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .username(user.getUsername())
            .roles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()))
            .build();
    }
    
    /**
     * Refresh access token using refresh token
     */
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        
        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        // Extract username
        String username = jwtTokenProvider.extractUsername(refreshToken);
        
        // Load user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Generate new access token
        UserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtTokenProvider.generateToken(userDetails);
        
        return AuthenticationResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)  // Reuse same refresh token
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .username(user.getUsername())
            .roles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()))
            .build();
    }
}
```

---

## JWT Security

### 1. Token Storage

**❌ localStorage (Vulnerable to XSS):**
```javascript
// DON'T DO THIS
localStorage.setItem('token', jwtToken);
```

**✅ HttpOnly Cookie (Better):**
```java
@PostMapping("/login")
public ResponseEntity<AuthenticationResponse> login(
        @RequestBody LoginRequest request,
        HttpServletResponse response) {
    
    AuthenticationResponse authResponse = authenticationService.login(request);
    
    // Set JWT in HttpOnly cookie
    Cookie cookie = new Cookie("jwt", authResponse.getAccessToken());
    cookie.setHttpOnly(true);
    cookie.setSecure(true);  // HTTPS only
    cookie.setPath("/");
    cookie.setMaxAge(24 * 60 * 60);  // 24 hours
    response.addCookie(cookie);
    
    return ResponseEntity.ok(authResponse);
}
```

### 2. Token Expiration

```java
// Short-lived access token (15-30 minutes)
private long jwtExpiration = 900000;  // 15 minutes

// Long-lived refresh token (7 days)
private long refreshExpiration = 604800000;  // 7 days
```

### 3. Token Blacklist (Logout)

```java
@Service
public class TokenBlacklistService {
    
    private final Set<String> blacklist = new HashSet<>();
    
    public void blacklistToken(String token) {
        blacklist.add(token);
    }
    
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}

// In JWT Filter
if (tokenBlacklistService.isBlacklisted(token)) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return;
}
```

### 4. Token Rotation

```java
public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
    // Validate old refresh token
    String oldRefreshToken = request.getRefreshToken();
    
    // Generate new tokens (both access and refresh)
    String newAccessToken = jwtTokenProvider.generateToken(userDetails);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);
    
    // Blacklist old refresh token
    tokenBlacklistService.blacklistToken(oldRefreshToken);
    
    return new AuthenticationResponse(newAccessToken, newRefreshToken);
}
```

---

## Best Practices

### 1. Use Strong Secret Keys

```java
// ❌ BAD - Weak secret
private String secretKey = "mysecret";

// ✅ GOOD - Strong secret (at least 256 bits for HS256, 512 bits for HS512)
private String secretKey = "mySecretKeyMustBeLongEnoughForHS512AlgorithmAtLeast512Bits";

// ✅ BETTER - Load from environment
@Value("${JWT_SECRET}")
private String secretKey;
```

### 2. Set Appropriate Expiration

```yaml
application:
  security:
    jwt:
      expiration: 900000  # 15 minutes for access token
      refresh-token-expiration: 604800000  # 7 days for refresh token
```

### 3. Validate Token Thoroughly

```java
public boolean validateToken(String token) {
    try {
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);
        return true;
    } catch (ExpiredJwtException e) {
        log.error("JWT token is expired");
    } catch (UnsupportedJwtException e) {
        log.error("JWT token is unsupported");
    } catch (MalformedJwtException e) {
        log.error("Invalid JWT token");
    } catch (SignatureException e) {
        log.error("Invalid JWT signature");
    } catch (IllegalArgumentException e) {
        log.error("JWT claims string is empty");
    }
    return false;
}
```

### 4. Don't Store Sensitive Data in JWT

```java
// ❌ BAD
claims.put("password", user.getPassword());
claims.put("ssn", user.getSocialSecurityNumber());

// ✅ GOOD
claims.put("sub", user.getUsername());
claims.put("roles", user.getRoles());
claims.put("email", user.getEmail());
```

### 5. Implement Refresh Token Rotation

```java
// Always generate new refresh token when refreshing access token
public AuthenticationResponse refreshToken(String oldRefreshToken) {
    // Validate old token
    // Generate new access token
    String newAccessToken = generateToken(userDetails);
    
    // Generate new refresh token
    String newRefreshToken = generateRefreshToken(userDetails);
    
    // Invalidate old refresh token
    blacklistToken(oldRefreshToken);
    
    return new AuthenticationResponse(newAccessToken, newRefreshToken);
}
```

---

## Working Demo

**🎯 Complete working JWT implementation:**
- **[JWT Authentication Demo](demo-jwt-auth/)**

This demo includes:
- ✅ Complete JWT generation and validation
- ✅ User registration and login
- ✅ Access token and refresh token
- ✅ Protected endpoints with @PreAuthorize
- ✅ Role-based access control
- ✅ Password encoding with BCrypt
- ✅ Custom exception handling
- ✅ Comprehensive README with testing

---

## Interview Questions

### Q1: Explain how JWT authentication works in Spring Security.

**Answer:**

JWT authentication is a stateless authentication mechanism:

**Flow:**
```
1. User Login:
   - User sends credentials (username/password)
   - Server validates credentials
   - Server generates JWT token
   - Returns token to client

2. Subsequent Requests:
   - Client sends JWT in Authorization header
   - JwtAuthenticationFilter extracts token
   - Token is validated (signature, expiration)
   - UserDetails loaded from token claims
   - Authentication set in SecurityContext
   - Request proceeds with authenticated user

3. Protected Resource Access:
   - Spring Security checks authentication
   - Verifies roles/permissions
   - Grants or denies access
```

**Key Components:**
- `JwtTokenProvider`: Generate and validate tokens
- `JwtAuthenticationFilter`: Extract and validate tokens from requests
- `SecurityConfig`: Configure stateless session management
- `UserDetailsService`: Load user details

---

### Q2: What are the security risks of JWT and how do you mitigate them?

**Answer:**

**Risks and Mitigations:**

1. **XSS (Cross-Site Scripting)**
   - Risk: If stored in localStorage, JWT can be stolen via XSS
   - Mitigation: Store in HttpOnly cookie, implement CSP headers

2. **Token Theft**
   - Risk: JWT can be stolen and replayed
   - Mitigation: Short expiration times, HTTPS only, refresh token rotation

3. **No Built-in Revocation**
   - Risk: Can't immediately invalidate a JWT
   - Mitigation: Token blacklist, short expiration, refresh tokens

4. **Information Disclosure**
   - Risk: JWT payload is Base64 encoded, not encrypted
   - Mitigation: Don't store sensitive data in JWT, use encryption if needed

5. **Weak Secret Key**
   - Risk: Weak key can be brute-forced
   - Mitigation: Use strong secret (512+ bits), rotate regularly

**Example Mitigation:**
```java
// Short-lived access token
private long accessTokenExpiration = 900000;  // 15 minutes

// HttpOnly cookie
cookie.setHttpOnly(true);
cookie.setSecure(true);

// Token blacklist for logout
tokenBlacklistService.blacklistToken(token);

// Strong secret from environment
@Value("${JWT_SECRET}")
private String secretKey;
```

---

### Q3: Explain the difference between Access Token and Refresh Token.

**Answer:**

| Aspect | Access Token | Refresh Token |
|--------|--------------|---------------|
| **Purpose** | Access protected resources | Get new access token |
| **Expiration** | Short (15-30 min) | Long (7-30 days) |
| **Usage Frequency** | Every API request | Only when access token expires |
| **Storage** | Memory/HttpOnly cookie | HttpOnly cookie (more secure) |
| **Security** | Less sensitive (short-lived) | More sensitive (long-lived) |
| **Revocation** | Difficult | Should be revocable |

**Flow:**
```
1. Login → Get both access token + refresh token
2. API calls → Use access token
3. Access token expires → Use refresh token to get new access token
4. Refresh token expires → User must login again
```

**Implementation:**
```java
// Access token - short lived
public String generateAccessToken(UserDetails userDetails) {
    return buildToken(userDetails, 900000);  // 15 minutes
}

// Refresh token - long lived
public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(userDetails, 604800000);  // 7 days
}

// Refresh endpoint
@PostMapping("/refresh")
public AuthenticationResponse refresh(@RequestBody RefreshTokenRequest request) {
    String refreshToken = request.getRefreshToken();
    
    if (validateToken(refreshToken)) {
        String username = extractUsername(refreshToken);
        UserDetails userDetails = loadUserByUsername(username);
        
        String newAccessToken = generateAccessToken(userDetails);
        String newRefreshToken = generateRefreshToken(userDetails);
        
        return new AuthenticationResponse(newAccessToken, newRefreshToken);
    }
    
    throw new RuntimeException("Invalid refresh token");
}
```

---

## Additional Resources

- [JWT.io](https://jwt.io/) - Decode and verify JWT tokens
- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [OAuth 2.0 RFC 6749](https://tools.ietf.org/html/rfc6749)

---

## Next Steps

- **[Demo Application](demo-jwt-auth/)** - Complete JWT implementation
- **[Section 03 - OAuth2](../03-oauth2/)** - OAuth2 and OpenID Connect

---

**Secure your APIs with JWT! 🔐**
