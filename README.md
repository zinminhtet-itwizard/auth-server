# Auth Server

A Spring Boot OAuth2 Authorization Server built with Spring Authorization Server, providing OAuth2 and OpenID Connect (OIDC) capabilities.

## Tech Stack

- Java 21+
- Spring Boot 4.x
- Spring Authorization Server (Spring Security 7)
- PostgreSQL (production) / H2 (development)
- Flyway migrations
- Thymeleaf (login & consent pages)
- Gradle (Kotlin DSL)

## Features

- Authorization Code + PKCE, Client Credentials, Refresh Token grant types
- JWT access tokens signed with RSA, with custom `roles` claim
- JDBC-backed client registration, authorization, and consent storage
- Custom login and OAuth2 consent pages
- OIDC discovery and UserInfo endpoints
- Database-backed user authentication with BCrypt password hashing
- Dev/prod profile separation

## Quick Start

```bash
# Run with dev profile (H2 in-memory database)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

The server starts at `http://localhost:9000`.

### Dev Credentials

| Type   | ID / Username | Secret / Password |
|--------|---------------|-------------------|
| Client | demo-client   | demo-secret       |
| User   | admin         | admin             |

### Key Endpoints

| Endpoint                              | Description             |
|---------------------------------------|-------------------------|
| `/login`                              | Login page              |
| `/oauth2/authorize`                   | Authorization endpoint  |
| `/oauth2/token`                       | Token endpoint          |
| `/oauth2/jwks`                        | JWK Set endpoint        |
| `/.well-known/openid-configuration`   | OIDC discovery          |
| `/h2-console`                         | H2 database console (dev only) |

## Build & Test

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.auth.server.OAuth2AuthorizationServerTests"
```

## Production

Set the following environment variables:

```bash
DB_URL=jdbc:postgresql://localhost:5432/authdb
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
ISSUER_URL=https://auth.yourdomain.com
```

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## Project Structure

```
src/main/java/com/auth/server/
├── config/         # Security, token, and data initializer configuration
├── controller/     # Login and consent controllers
├── entity/         # User, Role JPA entities
├── repository/     # Spring Data JPA repositories
└── service/        # UserDetailsService implementation

src/main/resources/
├── db/migration/   # Flyway SQL scripts
├── templates/      # Thymeleaf templates (login, consent)
├── application.yaml
├── application-dev.yaml
├── application-prod.yaml
└── logback-spring.xml
```
