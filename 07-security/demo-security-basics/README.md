# Spring Security Basics Demo

A comprehensive demonstration of Spring Security 6 fundamentals including authentication, authorization, and access control.

## Overview

This demo showcases:
- **Form-based Login** - Custom login page with username/password
- **HTTP Basic Auth** - REST API protection
- **Role-Based Access Control (RBAC)** - USER, MANAGER, ADMIN roles
- **URL-based Security** - Using `authorizeHttpRequests()`
- **Method-level Security** - Using `@PreAuthorize` and `@Secured`
- **Remember-Me** - Persistent authentication across sessions
- **Custom UserDetailsService** - Database-backed authentication
- **Password Encoding** - BCrypt hashing

## Quick Start

### Running the Application

```bash
cd 07-security/demo-security-basics
mvn spring-boot:run
```

Application runs on: http://localhost:8085

### Default Credentials

| Username | Password    | Roles                    |
|----------|-------------|--------------------------|
| admin    | admin123    | ADMIN, MANAGER, USER     |
| manager  | manager123  | MANAGER, USER            |
| user     | user123     | USER                     |

## Testing the Demo

### Web UI (Form Login)

1. Open http://localhost:8085
2. Click "Login"
3. Enter credentials (e.g., `user` / `user123`)
4. Navigate to different pages to see role-based access

### REST API (HTTP Basic)

```bash
# Public endpoint (no auth required)
curl http://localhost:8085/api/public/health

# User endpoint (any authenticated user)
curl -u user:user123 http://localhost:8085/api/user/whoami

# Manager endpoint
curl -u manager:manager123 http://localhost:8085/api/manager/reports

# Admin endpoint
curl -u admin:admin123 http://localhost:8085/api/admin/users

# Try unauthorized access (will fail)
curl -u user:user123 http://localhost:8085/api/admin/users
```

## Project Structure

```
demo-security-basics/
├── src/main/java/com/masterclass/security/basics/
│   ├── SecurityBasicsApplication.java    # Main application
│   ├── config/
│   │   ├── SecurityConfig.java          # Security filter chains
│   │   └── DataInitializer.java         # Seeds demo users
│   ├── entity/
│   │   ├── User.java                    # User entity
│   │   └── Role.java                    # Role entity
│   ├── repository/
│   │   ├── UserRepository.java          # User data access
│   │   └── RoleRepository.java          # Role data access
│   ├── service/
│   │   └── CustomUserDetailsService.java # UserDetailsService impl
│   └── controller/
│       ├── WebController.java           # Thymeleaf controllers
│       └── ApiController.java           # REST API controllers
├── src/main/resources/
│   ├── application.yml                  # Configuration
│   └── templates/                       # Thymeleaf templates
│       ├── home.html
│       ├── login.html
│       ├── dashboard.html
│       ├── admin.html
│       └── ...
└── pom.xml
```

## Security Configuration Explained

### Two Security Filter Chains

```java
// Chain 1: REST API with HTTP Basic (stateless)
@Bean
@Order(1)
public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) {
    http.securityMatcher("/api/**")
        .httpBasic(...)
        .sessionManagement(session -> 
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
}

// Chain 2: Web UI with Form Login (stateful)
@Bean
@Order(2)
public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) {
    http.formLogin(form -> form.loginPage("/login")...)
        .logout(...)
        .rememberMe(...);
}
```

### URL-based Access Control

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/", "/home").permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
    .anyRequest().authenticated()
);
```

### Method-level Security

```java
@PreAuthorize("hasRole('ADMIN')")
public String adminUsers() { ... }

@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public String managerReports() { ... }

@Secured("ROLE_ADMIN")
public String adminSettings() { ... }
```

## Key Concepts

### 1. UserDetailsService

Loads user from database and converts to Spring Security's `UserDetails`:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(...));
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.getAuthorities()
        );
    }
}
```

### 2. Password Encoding

Always encode passwords with BCrypt:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### 3. Role vs Authority

- **Role**: Prefixed with `ROLE_` internally (e.g., `ROLE_ADMIN`)
- Use `hasRole("ADMIN")` - automatically adds `ROLE_` prefix
- Use `hasAuthority("ROLE_ADMIN")` - requires exact match

## H2 Console

Access database at: http://localhost:8085/h2-console

- JDBC URL: `jdbc:h2:mem:securitydb`
- Username: `sa`
- Password: (empty)

## Technologies Used

- Spring Boot 3.2.0
- Spring Security 6
- Spring Data JPA
- H2 Database
- Thymeleaf + Security Extras
- BCrypt Password Encoding

## Next Steps

After mastering this demo, proceed to:
- `demo-jwt-auth` - JWT token-based authentication
- `demo-oauth2-client` - OAuth2 social login
