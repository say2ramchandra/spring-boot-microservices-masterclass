# Module 07: Security in Microservices

> **Secure your microservices with Spring Security, OAuth2, and JWT**

## 📚 Module Overview

Learn to implement authentication, authorization, and security best practices in microservices architecture.

---

## 🚀 Runnable Demos

| Demo | Port | Description |
|------|------|-------------|
| [demo-security-basics](demo-security-basics/) | 8085 | Form login, HTTP Basic, RBAC |
| [demo-jwt-auth](demo-jwt-auth/) | 8086 | JWT token authentication |
| [demo-oauth2](demo-oauth2/) | 8087 | OAuth2 social login (Google/GitHub) |

### Quick Start

```bash
# Security Basics Demo (Form Login + HTTP Basic)
cd 07-security/demo-security-basics
mvn spring-boot:run
# Open http://localhost:8085
# Login: user/user123, manager/manager123, admin/admin123

# JWT Authentication Demo
cd 07-security/demo-jwt-auth
mvn spring-boot:run
# Login: curl -X POST http://localhost:8086/api/auth/login \
#   -H "Content-Type: application/json" \
#   -d '{"username":"user","password":"user123"}'

# OAuth2 Demo (requires OAuth2 credentials)
cd 07-security/demo-oauth2
mvn spring-boot:run
# Open http://localhost:8087
```

### Demo Features

**demo-security-basics**
- Custom login page with form authentication
- HTTP Basic for REST API
- Role-based access (USER, MANAGER, ADMIN)
- Method-level security (@PreAuthorize)
- Remember-me functionality
- BCrypt password encoding

**demo-jwt-auth**
- JWT token generation and validation
- Refresh token handling
- Stateless authentication
- Token expiration and renewal
- Role-based API access

**demo-oauth2**
- Google OAuth2 login
- GitHub OAuth2 login
- User info extraction
- User persistence from OAuth2

---

## 🎯 Learning Objectives

- ✅ Implement authentication with Spring Security
- ✅ Use OAuth2 and OpenID Connect
- ✅ Generate and validate JWT tokens
- ✅ Secure microservice communication
- ✅ Implement API Gateway security
- ✅ Handle CORS and CSRF
- ✅ Apply security best practices

---

## 🔐 Security Fundamentals

### Authentication vs Authorization

```
Authentication: "Who are you?"
├─ Username/Password
├─ OAuth2 (Google, GitHub)
├─ JWT tokens
└─ Biometrics

Authorization: "What can you do?"
├─ Roles (ADMIN, USER)
├─ Permissions (READ, WRITE)
├─ Resource-based access
└─ Attribute-based access
```

---

## 🛡️ Spring Security Basics

### Basic Authentication

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());  // For APIs
        
        return http.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("user")
            .password("{noop}password")  // {noop} = no encoding
            .roles("USER")
            .build();
            
        UserDetails admin = User.builder()
            .username("admin")
            .password("{bcrypt}$2a$10$...")  // BCrypt encoded
            .roles("ADMIN", "USER")
            .build();
            
        return new InMemoryUserDetailsManager(user, admin);
    }
}
```

---

## 🎫 JWT (JSON Web Tokens)

### JWT Structure

```
Header.Payload.Signature

Example:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

Decoded:
{
  "alg": "HS256",
  "typ": "JWT"
}
{
  "sub": "1234567890",
  "name": "John Doe",
  "iat": 1516239022,
  "exp": 1516242622,
  "roles": ["USER", "ADMIN"]
}
```

### JWT Implementation

**Token Generation**:
```java
@Service
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;  // milliseconds
    
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim("roles", userDetails.getAuthorities())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
        
        return claims.getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.error("Invalid JWT token", ex);
            return false;
        }
    }
}
```

**JWT Filter**:
```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = extractTokenFromRequest(request);
            
            if (token != null && tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

**Login Endpoint**:
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
        return ResponseEntity.ok(new JwtAuthResponse(token));
    }
}

@Data
class LoginRequest {
    private String username;
    private String password;
}

@Data
@AllArgsConstructor
class JwtAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
}
```

---

## 🔐 OAuth2 & OpenID Connect

### OAuth2 Flow

```
Resource Owner (User)
       │
       │ 1. Login Request
       ↓
Authorization Server (Keycloak/Auth0)
       │
       │ 2. Authorization Code
       ↓
Client Application
       │
       │ 3. Exchange Code for Token
       ↓
Authorization Server
       │
       │ 4. Access Token
       ↓
Client Application
       │
       │ 5. API Request + Token
       ↓
Resource Server (Your API)
       │
       │ 6. Validate Token
       ↓
Authorization Server
       │
       │ 7. Token Valid
       ↓
Resource Server
       │
       │ 8. Protected Resource
       ↓
Client Application
```

### OAuth2 Implementation

**Configuration**:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-auth-server.com/realms/your-realm
          jwk-set-uri: https://your-auth-server.com/realms/your-realm/protocol/openid-connect/certs
```

**Security Config**:
```java
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );
        
        return http.build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = 
            new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        
        JwtAuthenticationConverter jwtAuthenticationConverter = 
            new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
            grantedAuthoritiesConverter);
        
        return jwtAuthenticationConverter;
    }
}
```

---

## 🌐 API Gateway Security

### Securing the Gateway

```java
@Configuration
public class GatewaySecurityConfig {
    
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http) {
        
        http
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/api/auth/**").permitAll()
                .pathMatchers("/api/admin/**").hasRole("ADMIN")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            )
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}
```

### Token Relay

**Gateway forwards token to downstream services**:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product_service
          uri: lb://PRODUCT-SERVICE
          predicates:
            - Path=/api/products/**
          filters:
            - TokenRelay=  # Forward OAuth2 token
            - RemoveRequestHeader=Cookie
```

---

## 🔒 Microservice-to-Microservice Security

### Service-to-Service Authentication

**Option 1: Shared Secret** (Simple but less secure)
```java
@Configuration
public class ServiceSecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/internal/**").hasRole("SERVICE")
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new ServiceAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
            );
        
        return http.build();
    }
}

public class ServiceAuthenticationFilter extends OncePerRequestFilter {
    
    @Value("${service.secret}")
    private String serviceSecret;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String secret = request.getHeader("X-Service-Secret");
        
        if (serviceSecret.equals(secret)) {
            SecurityContextHolder.getContext().setAuthentication(
                new ServiceAuthentication());
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**Option 2: mTLS (Mutual TLS)** (More secure)
```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: password
    key-store-type: PKCS12
    key-alias: service
    
    # Client certificate validation
    client-auth: need
    trust-store: classpath:truststore.p12
    trust-store-password: password
```

---

## 🛡️ Security Best Practices

### 1. Password Encoding

```java
@Configuration
public class PasswordConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt with strength 12
        return new BCryptPasswordEncoder(12);
    }
}

@Service
public class UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(SignupRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }
}
```

### 2. CORS Configuration

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://yourdomain.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### 3. Rate Limiting

```java
@Configuration
public class RateLimitingConfig {
    
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(
            exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id")
        );
    }
    
    @Bean
    public RedisRateLimiter rateLimiter() {
        return new RedisRateLimiter(
            10,   // replenishRate: tokens per second
            20    // burstCapacity: maximum tokens
        );
    }
}
```

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product_route
          uri: lb://PRODUCT-SERVICE
          predicates:
            - Path=/api/products/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
                key-resolver: "#{@userKeyResolver}"
```

### 4. Input Validation

```java
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        // Validation happens automatically
        return ResponseEntity.ok(userService.create(request));
    }
}

@Data
public class CreateUserRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain uppercase, lowercase, number, and special character"
    )
    private String password;
}
```

### 5. SQL Injection Prevention

```java
// ✅ GOOD - Using parameterized queries
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
}

// ✅ GOOD - Using JPA query methods
Optional<User> findByUsernameAndEmail(String username, String email);

// ❌ BAD - Don't concatenate SQL strings
@Query(value = "SELECT * FROM users WHERE username = '" + username + "'", nativeQuery = true)
User findByUsernameBad(String username);  // Vulnerable to SQL injection!
```

### 6. Secrets Management

```yaml
# ❌ BAD - Don't commit secrets to Git
spring:
  datasource:
    password: mySecretPassword123

# ✅ GOOD - Use environment variables
spring:
  datasource:
    password: ${DB_PASSWORD}

# ✅ BETTER - Use secrets management service
spring:
  cloud:
    vault:
      token: ${VAULT_TOKEN}
      scheme: https
      host: vault.example.com
      port: 8200
      kv:
        enabled: true
```

---

## 🎓 Security Checklist

### Development

- [ ] Use HTTPS everywhere
- [ ] Implement proper authentication
- [ ] Use strong password encoding (BCrypt)
- [ ] Validate all inputs
- [ ] Sanitize outputs (prevent XSS)
- [ ] Use parameterized queries (prevent SQL injection)
- [ ] Implement rate limiting
- [ ] Configure CORS properly
- [ ] Use secure headers
- [ ] Keep dependencies updated

### Production

- [ ] Rotate secrets regularly
- [ ] Monitor security events
- [ ] Implement audit logging
- [ ] Use Web Application Firewall (WAF)
- [ ] Regular security audits
- [ ] Penetration testing
- [ ] Incident response plan
- [ ] Data encryption at rest and in transit

---

## 📚 Security Testing

### Unit Testing

```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTests {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithAnonymousUser
    void accessProtectedEndpoint_asAnonymous_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void accessProtectedEndpoint_asUser_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void accessAdminEndpoint_asUser_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
            .andExpect(status().isForbidden());
    }
}
```

---

_Secure your microservices! 🔒_
