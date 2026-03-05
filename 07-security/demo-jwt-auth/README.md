# JWT Authentication Demo

A comprehensive demonstration of JWT (JSON Web Token) based authentication for REST APIs using Spring Security 6.

## Overview

This demo showcases:
- **JWT Token Generation** - Creating signed tokens with claims
- **Token Validation** - Verifying token signature and expiration
- **Refresh Tokens** - Rotating tokens without re-authentication
- **Stateless Authentication** - No server-side session storage
- **Role-Based Access Control** - Protecting endpoints by role
- **Method-Level Security** - Using `@PreAuthorize` annotations

## Quick Start

### Running the Application

```bash
cd 07-security/demo-jwt-auth
mvn spring-boot:run
```

Application runs on: http://localhost:8086

### Default Credentials

| Username | Password    | Roles                    |
|----------|-------------|--------------------------|
| admin    | admin123    | ADMIN, MANAGER, USER     |
| manager  | manager123  | MANAGER, USER            |
| user     | user123     | USER                     |

## API Usage

### 1. Login (Get Tokens)

```bash
# Login as user
curl -X POST http://localhost:8086/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "username": "user",
  "email": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

### 2. Access Protected Endpoints

```bash
# Store token in variable
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Access user endpoint
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8086/api/user/profile

# Access manager endpoint (requires MANAGER role)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8086/api/manager/dashboard

# Access admin endpoint (requires ADMIN role)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8086/api/admin/users
```

### 3. Refresh Token

```bash
curl -X POST http://localhost:8086/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"550e8400-e29b-41d4-a716-446655440000"}'
```

### 4. Logout

```bash
curl -X POST http://localhost:8086/api/auth/logout \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"550e8400-e29b-41d4-a716-446655440000"}'
```

## API Endpoints

### Public Endpoints (No Auth)
| Method | Endpoint              | Description           |
|--------|----------------------|----------------------|
| GET    | /api/public/health   | Health check         |
| GET    | /api/public/info     | API information      |
| POST   | /api/auth/login      | Login and get tokens |
| POST   | /api/auth/refresh    | Refresh access token |
| POST   | /api/auth/logout     | Invalidate token     |

### Protected Endpoints (JWT Required)
| Method | Endpoint                | Required Role    |
|--------|------------------------|------------------|
| GET    | /api/user/profile      | Any authenticated|
| GET    | /api/user/data         | Any authenticated|
| GET    | /api/manager/dashboard | MANAGER or ADMIN |
| GET    | /api/manager/reports   | MANAGER or ADMIN |
| GET    | /api/admin/dashboard   | ADMIN only       |
| GET    | /api/admin/users       | ADMIN only       |
| GET    | /api/admin/settings    | ADMIN only       |

## Project Structure

```
demo-jwt-auth/
├── src/main/java/com/masterclass/security/jwt/
│   ├── JwtAuthApplication.java           # Main application
│   ├── config/
│   │   ├── SecurityConfig.java          # Security configuration
│   │   └── DataInitializer.java         # Seed demo users
│   ├── dto/
│   │   ├── LoginRequest.java            # Login request DTO
│   │   ├── JwtResponse.java             # Token response DTO
│   │   └── RefreshTokenRequest.java     # Refresh request DTO
│   ├── entity/
│   │   ├── User.java                    # User entity
│   │   ├── Role.java                    # Role entity
│   │   └── RefreshToken.java            # Refresh token entity
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java # JWT filter
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── RoleRepository.java
│   │   └── RefreshTokenRepository.java
│   ├── service/
│   │   ├── JwtService.java              # JWT operations
│   │   ├── RefreshTokenService.java     # Refresh token logic
│   │   └── CustomUserDetailsService.java
│   └── controller/
│       ├── AuthController.java          # Auth endpoints
│       └── ApiController.java           # Protected API
├── src/main/resources/
│   └── application.yml                  # Configuration
└── pom.xml
```

## JWT Flow

```
1. Login Request
   Client → POST /api/auth/login → Server
   
2. Authentication
   Server validates credentials → Generates JWT + Refresh Token
   
3. Token Response
   Server → {accessToken, refreshToken} → Client
   
4. Protected Request
   Client → GET /api/user/profile + Authorization: Bearer <token> → Server
   
5. JWT Validation
   JwtAuthenticationFilter extracts token → Validates → Sets SecurityContext
   
6. Response
   Server → Protected data → Client
   
7. Token Refresh (when access token expires)
   Client → POST /api/auth/refresh {refreshToken} → Server → New access token
```

## Key Concepts

### JWT Structure

```
Header.Payload.Signature

Header: {"alg": "HS256", "typ": "JWT"}
Payload: {"sub": "user", "roles": ["ROLE_USER"], "iat": 1234567890, "exp": 1234571490}
Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

### Token Configuration

```yaml
jwt:
  secret: YourSuperSecretKey...  # Min 256 bits for HS256
  expiration: 3600000            # Access token: 1 hour
  refresh-expiration: 86400000   # Refresh token: 24 hours
```

### Security Filter Chain

```java
http
    .csrf(csrf -> csrf.disable())              // Stateless, no CSRF needed
    .sessionManagement(session -> 
        session.sessionCreationPolicy(STATELESS))  // No sessions
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```

## Testing with Different Users

```bash
# Login as admin (all endpoints accessible)
curl -X POST http://localhost:8086/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Login as manager (user + manager endpoints)
curl -X POST http://localhost:8086/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"manager","password":"manager123"}'

# Login as user (only user endpoints)
curl -X POST http://localhost:8086/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}'
```

## Technologies Used

- Spring Boot 3.2.0
- Spring Security 6
- JJWT 0.12.3 (JSON Web Token library)
- Spring Data JPA
- H2 Database
- Lombok

## Common Issues

### Invalid Token Error
- Check token hasn't expired
- Verify token format: `Bearer <token>`
- Ensure secret key matches

### 403 Forbidden
- User doesn't have required role
- Check `@PreAuthorize` annotation

### Token Expired
- Use refresh token to get new access token
- If refresh token expired, re-login

## Next Steps

After mastering JWT authentication, explore:
- **demo-oauth2-client** - OAuth2 social login (Google, GitHub)
- Implement token blacklisting
- Add multiple device support
- Implement sliding window tokens
