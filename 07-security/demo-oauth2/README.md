# OAuth2 Authentication Demo

A demonstration of OAuth2 social login integration with Google and GitHub using Spring Security 6.

## Overview

This demo showcases:
- **OAuth2 Login** - "Sign in with Google/GitHub" functionality
- **Authorization Code Grant** - Standard OAuth2 flow
- **Custom OAuth2UserService** - Processing user info from providers
- **User Persistence** - Saving OAuth2 users to database
- **Multiple Providers** - Google and GitHub integration

## Quick Start

### Prerequisites

To use OAuth2 social login, you need to configure credentials:

#### 1. Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Navigate to "APIs & Services" → "Credentials"
4. Click "Create Credentials" → "OAuth client ID"
5. Choose "Web application"
6. Add authorized redirect URI: `http://localhost:8087/login/oauth2/code/google`
7. Copy the Client ID and Client Secret

#### 2. GitHub OAuth2 Setup

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Click "New OAuth App"
3. Fill in:
   - Application name: `OAuth2 Demo`
   - Homepage URL: `http://localhost:8087`
   - Authorization callback URL: `http://localhost:8087/login/oauth2/code/github`
4. Copy the Client ID and generate Client Secret

### Running the Application

Set environment variables and run:

```bash
# Set OAuth2 credentials
export GOOGLE_CLIENT_ID=your-google-client-id
export GOOGLE_CLIENT_SECRET=your-google-client-secret
export GITHUB_CLIENT_ID=your-github-client-id
export GITHUB_CLIENT_SECRET=your-github-client-secret

# Run the application
cd 07-security/demo-oauth2
mvn spring-boot:run
```

Or configure in `application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-google-client-id
            client-secret: your-google-client-secret
          github:
            client-id: your-github-client-id
            client-secret: your-github-client-secret
```

Application runs on: http://localhost:8087

## OAuth2 Flow

```
┌──────────┐     1. Click "Login with Google"      ┌──────────────┐
│          │ ────────────────────────────────────► │              │
│  User    │                                       │  Application │
│          │ ◄──── 2. Redirect to Google ──────── │  (Spring)    │
└──────────┘                                       └──────────────┘
     │                                                    │
     │ 3. User authenticates                              │
     │    with Google                                     │
     ▼                                                    │
┌──────────────┐                                          │
│              │  4. User grants permissions              │
│   Google     │                                          │
│              │  5. Redirect with authorization code     │
└──────────────┘ ─────────────────────────────────────────┘
                          │
                          │ 6. Exchange code for tokens
                          │ 7. Fetch user info
                          │ 8. Save to database
                          ▼
                 ┌──────────────┐
                 │  Dashboard   │  User is authenticated!
                 └──────────────┘
```

## Project Structure

```
demo-oauth2/
├── src/main/java/com/masterclass/security/oauth2/
│   ├── OAuth2Application.java           # Main application
│   ├── config/
│   │   └── SecurityConfig.java          # OAuth2 security config
│   ├── entity/
│   │   └── User.java                    # User entity
│   ├── repository/
│   │   └── UserRepository.java          # User data access
│   ├── service/
│   │   └── CustomOAuth2UserService.java # OAuth2 user processing
│   └── controller/
│       └── WebController.java           # Web pages
├── src/main/resources/
│   ├── application.yml                  # OAuth2 configuration
│   └── templates/
│       ├── home.html
│       ├── login.html
│       ├── dashboard.html
│       └── profile.html
└── pom.xml
```

## Key Components

### SecurityConfig

```java
http.oauth2Login(oauth2 -> oauth2
    .loginPage("/login")
    .userInfoEndpoint(userInfo -> userInfo
        .userService(customOAuth2UserService)
    )
    .successHandler(successHandler)
    .failureUrl("/login?error=true")
);
```

### CustomOAuth2UserService

Processes user info from OAuth2 providers and saves to database:

```java
@Override
public OAuth2User loadUser(OAuth2UserRequest userRequest) {
    OAuth2User oAuth2User = delegate.loadUser(userRequest);
    
    // Extract user info
    String email = extractEmail(attributes, provider);
    String name = extractName(attributes, provider);
    
    // Save to database
    userRepository.save(user);
    
    return oAuth2User;
}
```

### User Entity

```java
@Entity
public class User {
    private Long id;
    private String email;
    private String name;
    private String imageUrl;
    private AuthProvider provider;  // GOOGLE, GITHUB
    private String providerId;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
```

## Endpoints

| Endpoint                              | Description                    | Auth Required |
|--------------------------------------|--------------------------------|---------------|
| `/`                                  | Home page                      | No            |
| `/login`                             | Login page with OAuth2 buttons | No            |
| `/oauth2/authorization/google`       | Start Google OAuth2 flow       | No            |
| `/oauth2/authorization/github`       | Start GitHub OAuth2 flow       | No            |
| `/login/oauth2/code/google`          | Google callback (automatic)    | No            |
| `/login/oauth2/code/github`          | GitHub callback (automatic)    | No            |
| `/dashboard`                         | User dashboard                 | Yes           |
| `/profile`                           | User profile                   | Yes           |

## Provider-Specific Attributes

### Google User Attributes
- `sub` - Unique user ID
- `name` - Full name
- `email` - Email address
- `picture` - Profile picture URL
- `email_verified` - Email verification status

### GitHub User Attributes
- `id` - Unique user ID
- `login` - Username
- `name` - Full name
- `email` - Email (may be null)
- `avatar_url` - Profile picture URL

## Configuration Options

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope:
              - email
              - profile
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
          
          github:
            client-id: ${GITHUB_CLIENT_ID}
            client-secret: ${GITHUB_CLIENT_SECRET}
            scope:
              - user:email
              - read:user
```

## Testing Without Real Credentials

Without real OAuth2 credentials, you can still:
1. Explore the code structure
2. Understand the OAuth2 flow
3. See the UI components
4. Review the configuration

The application will show an error when clicking OAuth2 buttons until valid credentials are configured.

## Security Considerations

1. **Never commit credentials** - Use environment variables
2. **Use HTTPS in production** - OAuth2 requires secure connections
3. **Validate redirect URIs** - Prevent open redirect attacks
4. **Scope minimization** - Only request necessary permissions
5. **Token storage** - Spring Security handles token lifecycle

## Technologies Used

- Spring Boot 3.2.0
- Spring Security 6 OAuth2 Client
- Spring Data JPA
- H2 Database
- Thymeleaf

## Related Demos

- `demo-security-basics` - Form login and basic security
- `demo-jwt-auth` - JWT token authentication
