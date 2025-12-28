# Section 01: Spring Security Basics

## Overview

Spring Security is the de-facto standard for securing Spring-based applications. This section covers the fundamentals of authentication, authorization, and securing REST APIs.

## Table of Contents
1. [Core Concepts](#core-concepts)
2. [Getting Started](#getting-started)
3. [Authentication](#authentication)
4. [Authorization](#authorization)
5. [Password Encoding](#password-encoding)
6. [Security Configuration](#security-configuration)
7. [Best Practices](#best-practices)
8. [Interview Questions](#interview-questions)

---

## Core Concepts

### Authentication vs Authorization

| Aspect | Authentication | Authorization |
|--------|---------------|---------------|
| **Question** | "Who are you?" | "What can you do?" |
| **Purpose** | Verify identity | Check permissions |
| **When** | Login process | Every request |
| **Examples** | Username/Password, OAuth2, JWT | Roles, Permissions, ACL |
| **Result** | User identity established | Access granted/denied |

**Authentication Process:**
```
1. User provides credentials (username/password)
2. System verifies credentials
3. User identity is established
4. Authentication token is issued
```

**Authorization Process:**
```
1. User makes a request
2. System checks user's roles/permissions
3. System verifies if user can access resource
4. Access granted or denied
```

---

## Getting Started

### Dependencies

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Web (for REST APIs) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- JPA (for database) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- H2 Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### Default Behavior

When you add Spring Security dependency, it **automatically** secures all endpoints:
- Generates a random password (printed in console)
- Username: `user`
- All endpoints require authentication
- Form login enabled

**Console Output:**
```
Using generated security password: a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6
```

---

## Authentication

### In-Memory Authentication

**Simple setup for development/testing:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        
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
            .password("{noop}admin123")
            .roles("ADMIN", "USER")
            .build();
        
        return new InMemoryUserDetailsManager(user, admin);
    }
}
```

**Test with curl:**
```bash
curl -u user:password http://localhost:8080/api/users
curl -u admin:admin123 http://localhost:8080/api/admin
```

### Database Authentication

**User Entity:**

```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String email;
    
    private boolean enabled = true;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;  // ROLE_USER, ROLE_ADMIN
}
```

**Custom UserDetailsService:**

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .disabled(!user.isEnabled())
            .authorities(getAuthorities(user.getRoles()))
            .build();
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toSet());
    }
}
```

---

## Authorization

### Role-Based Access Control (RBAC)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/public/**", "/api/auth/**").permitAll()
                
                // Role-based access
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                
                // Method-specific access
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());  // Disable for REST APIs
        
        return http.build();
    }
}
```

### Method-Level Security

**Enable method security:**

```java
@Configuration
@EnableMethodSecurity  // Enables @PreAuthorize, @PostAuthorize, @Secured
public class MethodSecurityConfig {
}
```

**Use annotations on methods:**

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping
    @PreAuthorize("permitAll()")  // Anyone can access
    public List<Product> getAllProducts() {
        return productService.findAll();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")  // Must be logged in
    public Product getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")  // Only ADMIN role
    public Product createProduct(@RequestBody Product product) {
        return productService.save(product);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")  // Admin or owner
    public void deleteProduct(@PathVariable Long id, @RequestParam Long userId) {
        productService.delete(id);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")  // Multiple roles
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }
}
```

**SpEL Expressions:**

```java
// Check if user is authenticated
@PreAuthorize("isAuthenticated()")

// Check if user is anonymous
@PreAuthorize("isAnonymous()")

// Check role
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")

// Check authority (permission)
@PreAuthorize("hasAuthority('WRITE_PRIVILEGE')")
@PreAuthorize("hasAnyAuthority('WRITE_PRIVILEGE', 'DELETE_PRIVILEGE')")

// Check ownership
@PreAuthorize("#userId == authentication.principal.id")

// Complex expressions
@PreAuthorize("hasRole('ADMIN') or (#userId == authentication.principal.id and hasAuthority('UPDATE_OWN'))")

// Post-process result
@PostAuthorize("returnObject.owner == authentication.name")
```

---

## Password Encoding

### Why Encode Passwords?

**❌ Never store plaintext passwords:**
```java
// VERY BAD - Don't do this!
user.setPassword("password123");  // Stored as plaintext
```

**✅ Always encode passwords:**
```java
user.setPassword(passwordEncoder.encode("password123"));  // Stored as BCrypt hash
```

### Password Encoders

```java
@Configuration
public class PasswordConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Recommended
        // return new BCryptPasswordEncoder(12);  // Custom strength (4-31)
    }
}
```

**Available Encoders:**

| Encoder | Strength | Speed | Recommendation |
|---------|----------|-------|----------------|
| `BCryptPasswordEncoder` | High | Slow | ✅ **Recommended** |
| `Argon2PasswordEncoder` | Very High | Slower | ✅ Best for new apps |
| `SCryptPasswordEncoder` | High | Slow | ✅ Good alternative |
| `Pbkdf2PasswordEncoder` | Medium | Medium | ⚠️ Acceptable |
| `NoOpPasswordEncoder` | None | Fast | ❌ **Never use in production** |

### Password Encoding in Practice

**Registration:**

```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(SignupRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Create user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        // ✅ Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Assign default role
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(userRole));
        
        return userRepository.save(user);
    }
}
```

**Password Change:**

```java
@Service
public class UserService {
    
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        
        // Set new password (encoded)
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
```

---

## Security Configuration

### Complete Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configure authorization
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            
            // HTTP Basic authentication
            .httpBasic(basic -> basic
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            )
            
            // Form login (optional)
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/api/auth/login")
                .successHandler(new CustomAuthenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler)
                .permitAll()
            )
            
            // Logout
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
            )
            
            // CSRF protection (disable for stateless REST APIs)
            .csrf(csrf -> csrf.disable())
            
            // Session management
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // For JWT
                // .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // For session-based
                // .maximumSessions(1)  // Limit concurrent sessions
                // .maxSessionsPreventsLogin(true)  // Prevent new login if max sessions reached
            )
            
            // Exception handling
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
            );
        
        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

### Custom Exception Handlers

**Authentication Entry Point:**

```java
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"error\":\"Unauthorized\",\"message\":\"" + authException.getMessage() + "\"}"
        );
    }
}
```

**Access Denied Handler:**

```java
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"error\":\"Forbidden\",\"message\":\"You don't have permission to access this resource\"}"
        );
    }
}
```

---

## Best Practices

### 1. Use HTTPS in Production

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: server
  port: 8443
```

### 2. Implement Account Lockout

```java
@Service
public class LoginAttemptService {
    
    private static final int MAX_ATTEMPTS = 5;
    private final LoadingCache<String, Integer> attemptsCache;
    
    public LoginAttemptService() {
        attemptsCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) {
                    return 0;
                }
            });
    }
    
    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }
    
    public void loginFailed(String key) {
        int attempts = attemptsCache.getUnchecked(key);
        attemptsCache.put(key, attempts + 1);
    }
    
    public boolean isBlocked(String key) {
        return attemptsCache.getUnchecked(key) >= MAX_ATTEMPTS;
    }
}
```

### 3. Secure Headers

```java
http.headers(headers -> headers
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'")
    )
    .frameOptions(frame -> frame.deny())
    .xssProtection(xss -> xss.block(true))
    .contentTypeOptions(Customizer.withDefaults())
    .referrerPolicy(referrer -> referrer
        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)
    )
);
```

### 4. Audit Logging

```java
@Aspect
@Component
public class SecurityAuditAspect {
    
    @AfterReturning(
        pointcut = "@annotation(org.springframework.security.access.prepost.PreAuthorize)",
        returning = "result"
    )
    public void logAuthorization(JoinPoint joinPoint, Object result) {
        String username = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        
        log.info("User {} accessed method {}", 
            username, joinPoint.getSignature().getName());
    }
}
```

### 5. Input Validation

```java
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(
            @Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }
}

@Data
public class SignupRequest {
    
    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    private String username;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$")
    private String password;
}
```

---

## Interview Questions

### Q1: Explain the difference between authentication and authorization in Spring Security.

**Answer:**

**Authentication** is the process of verifying the identity of a user:
- "Who are you?"
- Happens during login
- Verifies credentials (username/password, tokens, etc.)
- Results in a `SecurityContext` with user details

**Authorization** is the process of determining what an authenticated user can do:
- "What are you allowed to do?"
- Happens on every request
- Checks roles, authorities, permissions
- Results in access granted or `AccessDeniedException`

**Example:**
```java
// Authentication - verify who the user is
Authentication auth = authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(username, password)
);

// Authorization - check what they can do
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { ... }
```

---

### Q2: How does Spring Security's SecurityContext work?

**Answer:**

`SecurityContext` holds the security information for the current thread of execution.

**How it works:**
```
1. Request arrives
2. Security filters authenticate the request
3. Authentication object is created
4. SecurityContext stores the Authentication
5. SecurityContextHolder provides access to SecurityContext
6. After request, SecurityContext is cleared
```

**Accessing current user:**
```java
// Get current authentication
Authentication auth = SecurityContextHolder.getContext().getAuthentication();

// Get username
String username = auth.getName();

// Get authorities
Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

// Check if authenticated
boolean isAuthenticated = auth.isAuthenticated();

// Get user details
UserDetails userDetails = (UserDetails) auth.getPrincipal();
```

**Thread Safety:**
```java
// ThreadLocal storage - each thread has its own SecurityContext
SecurityContextHolder.setStrategyName(
    SecurityContextHolder.MODE_THREADLOCAL  // Default
);

// For async operations
@Async
public void asyncMethod() {
    // Propagate security context
    SecurityContext context = SecurityContextHolder.getContext();
    // Use DelegatingSecurityContextRunnable
}
```

---

### Q3: Explain password encoding in Spring Security. Why is it important?

**Answer:**

Password encoding transforms plaintext passwords into irreversible hashes to protect user credentials.

**Why it's important:**
1. **Database Breach Protection**: Even if database is compromised, passwords can't be recovered
2. **Rainbow Table Prevention**: Salted hashes prevent precomputed hash attacks
3. **Compliance**: Required by GDPR, PCI-DSS, and other regulations

**How Spring Security handles it:**

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Registration
String encodedPassword = passwordEncoder.encode("plaintext");
user.setPassword(encodedPassword);

// Login verification
boolean matches = passwordEncoder.matches("plaintext", encodedPassword);
```

**BCrypt Benefits:**
- Adaptive hashing (adjustable cost factor)
- Automatic salting
- Slow by design (prevents brute force)
- Each hash is unique even for same password

**Example:**
```
Password: "password123"
BCrypt Hash 1: $2a$10$N9qo8uLOickgx2ZMRZoMye/IVI9d/8l3f9VC9GN6NZw2X/MF0q.Ga
BCrypt Hash 2: $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.
                ^^^^ - Salt (random, stored in hash)
```

---

## Additional Resources

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [OWASP Security Guidelines](https://owasp.org/)
- [BCrypt Calculator](https://bcrypt-generator.com/)

---

## Next Steps

- **[Section 02 - JWT Authentication](../02-jwt-authentication/)** - Stateless authentication with JWT
- **[Section 03 - OAuth2](../03-oauth2/)** - OAuth2 and OpenID Connect integration

---

**Module 07 is securing your microservices! 🔒**
