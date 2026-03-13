# Auth Server - Spring Boot OAuth2 Authorization Server

## Project Overview
A Spring Boot OAuth2 Authorization Server built with Spring Authorization Server (not the deprecated Spring Security OAuth).
Provides OAuth2 and OpenID Connect (OIDC) capabilities for authenticating users and authorizing client applications.

## Tech Stack
- **Java**: 21+ (LTS)
- **Spring Boot**: 4.x
- **Spring Authorization Server**: `spring-boot-starter-oauth2-authorization-server`
- **Database**: PostgreSQL (production), H2 (development/testing)
- **Migrations**: Flyway
- **Build Tool**: Gradle (Kotlin DSL)
- **Testing**: JUnit 5, Spring Security Test, Testcontainers

## Project Structure
```
src/
├── main/
│   ├── java/com/auth/server/
│   │   ├── config/          # Security and authorization server configuration
│   │   ├── entity/          # JPA entities (User, Role, etc.)
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── service/         # Business logic services
│   │   ├── controller/      # Web controllers (login, consent, user management)
│   │   ├── dto/             # Data transfer objects
│   │   └── exception/       # Custom exceptions and error handlers
│   └── resources/
│       ├── application.yaml           # Common config
│       ├── application-dev.yaml       # Dev profile (H2, debug logging)
│       ├── application-prod.yaml      # Prod profile (PostgreSQL, secure defaults)
│       ├── db/migration/             # Flyway migration scripts
│       └── templates/                # Thymeleaf templates (login, consent pages)
└── test/
    ├── java/com/auth/server/         # Unit and integration tests
    └── resources/                    # Test configuration
```

## Architecture & Design Decisions

### OAuth2 Grant Types Supported
- **Authorization Code + PKCE** — primary flow for web/mobile/SPA clients
- **Client Credentials** — service-to-service authentication
- **Refresh Token** — token renewal without re-authentication
- **Device Authorization** — optional, for CLI/IoT devices

### Token Strategy
- **JWT** (self-contained) access tokens signed with RSA key pairs
- Configurable token lifetimes (access: 15min, refresh: 7 days — adjust per environment)
- Key pairs stored outside the repo; loaded via config properties in production

### Persistence
- JDBC-backed `RegisteredClientRepository` (not in-memory)
- JDBC-backed `OAuth2AuthorizationService` and `OAuth2AuthorizationConsentService`
- Flyway manages all schema migrations with versioned scripts (`V1__`, `V2__`, etc.)

### User Authentication
- Form-based login with Spring Security
- Passwords hashed with BCrypt
- User/Role entities stored in the database

### OpenID Connect
- OIDC discovery endpoint: `/.well-known/openid-configuration`
- UserInfo endpoint enabled
- ID tokens include standard claims (sub, name, email)

## Build & Run Commands
```bash
# Build
./gradlew build

# Run (dev profile)
./gradlew bootRun --args='--spring.profiles.active=dev'

# Run tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.auth.server.SomeTest"

# Generate a fresh RSA key pair (for JWT signing) — dev only
# Production keys should be managed externally (vault, env vars, mounted secrets)
```

## Code Conventions
- Follow standard Java naming conventions (camelCase methods/fields, PascalCase classes)
- Use constructor injection (no field injection with `@Autowired`)
- Configuration classes go in `config/` package
- Keep controllers thin — business logic belongs in `service/` layer
- Use `record` types for DTOs where possible
- Write integration tests for all security-sensitive endpoints
- All SQL migrations must be idempotent and backwards-compatible

## Security Guidelines
- **Never** commit private keys, secrets, or credentials to the repository
- Use environment variables or Spring Cloud Config for sensitive properties
- Always use HTTPS in production
- Enable CSRF protection for browser-facing endpoints
- Configure CORS explicitly — do not use `allowAll`
- Validate all redirect URIs registered with clients
- Use BCrypt (strength 10+) for password hashing

## Spring Profiles
| Profile | Database | Logging | Use Case |
|---------|----------|---------|----------|
| `dev`   | H2 (in-memory) | DEBUG | Local development |
| `test`  | H2 / Testcontainers | WARN | Automated tests |
| `prod`  | PostgreSQL | INFO | Production deployment |

## Key Configuration Properties
```yaml
# Customize in application-{profile}.yaml
server.port: 9000
spring.datasource.url: jdbc:postgresql://localhost:5432/authdb
spring.jpa.hibernate.ddl-auto: validate  # Flyway handles schema
spring.security.oauth2.authorizationserver:
  issuer-url: http://localhost:9000
```

## Testing Strategy
- **Unit tests**: Service layer logic, token customization
- **Integration tests**: Full OAuth2 flows using `MockMvc` and `@SpringBootTest`
- **Testcontainers**: PostgreSQL container for realistic DB integration tests
- Test all grant type flows end-to-end
- Verify token contents (claims, scopes, expiry)

## Common Tasks

### Register a New OAuth2 Client
Add a client via the `RegisteredClientRepository` — either through a migration script,
a setup endpoint (admin-only), or a CLI runner on startup.

### Add a New Scope/Permission
1. Define the scope as a constant
2. Register it with relevant clients
3. Update resource server(s) to enforce the new scope

### Rotate JWT Signing Keys
1. Add the new key pair alongside the old one (support both during transition)
2. Update the `JWKSource` bean to advertise both keys
3. After all existing tokens expire, remove the old key
