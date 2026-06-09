ca# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Spring Boot REST API backend for a Cambodian government inspectorate management system (Ministry of Civil Reform). The UI language mixes Khmer (ខ្មែរ) and English — log messages and error strings in Khmer are intentional.

## Commands

All commands run from `inspectorate-management-system/` (the inner directory containing `pom.xml`).

```bash
# Start infrastructure (PostgreSQL, Redis, MinIO)
docker-compose up -d postgres redis minio

# Run the application
./mvnw spring-boot:run

# Build (skip tests)
./mvnw clean package -DskipTests

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName

# Run a single test method
./mvnw test -Dtest=ClassName#methodName
```

**API docs**: http://localhost:8080/swagger-ui.html  
**Health**: http://localhost:8080/actuator/health  
**MinIO console**: http://localhost:9001 (admin / password123)

**Default admin credentials** (seeded on first startup): `admin@system.kh` / `Admin@1234`

## Architecture

### Layer structure

Standard Spring Boot layered architecture: `Controller → Service (interface + impl) → Repository → Entity`. MapStruct `Mapper` interfaces handle DTO↔Entity conversion. All controllers return `ApiResponse<T>` (or `ApiResponse<PageResponse<T>>` for paginated lists).

### Key domain relationships

- **User ↔ Officer** (1:1): A `User` is the login account; `Officer` holds the HR profile (name, department, position, etc.). Not every officer has a user account; the link is optional.
- **Document → Approval**: Documents go through a DRAFT → PENDING → APPROVED/REJECTED lifecycle. An `Approval` record is created per approval request and drives document status changes.
- **Meeting → MeetingRoom / MeetingAttendee / MeetingMinute**: Meetings book a room (with conflict detection), track attendees with attendance status, and can have minutes attached.
- **Announcement → AnnouncementRecipient**: Announcements are targeted to specific recipients; read-status is tracked per recipient.
- **Attachment**: Files are stored in MinIO; the `Attachment` entity holds metadata. The path pattern is `{refType}/{refId}/{uuid}.{ext}`. `AttachmentRefType` enum maps domain objects to storage prefixes.

### Status / Lookup tables

Statuses are stored in `Lookup*` tables (e.g., `LookupDocumentStatus`) with bilingual labels (`labelKh`, `labelEn`) and a `statusCode` PK. Enums (`DocumentStatusCode`, `MeetingStatusCode`, etc.) mirror those codes for type-safe comparisons in service logic. `DataInitializer` seeds all lookup rows, roles, permissions, and the super-admin user on every clean startup (skips if rows already exist).

### Security

- Stateless JWT: access token (24 h) + refresh token (7 d). Both token types are distinguished by a `type` claim (`ACCESS` / `REFRESH`).
- Permissions are embedded in the access token's `permissions` claim as a `List<String>`. Spring Security loads them as `GrantedAuthority` via `JwtAuthenticationFilter`.
- Fine-grained access is enforced in `WebSecurityConfig` (URL-level) and via `@PreAuthorize` / `SecurityUtils.hasPermission()` in service methods.
- Token blacklisting on logout: revoked tokens are stored in Redis under `blacklist:{token}` until their natural expiry.
- Account lockout: 5 failed logins triggers a 30-minute lock (`lockedUntil` column on `User`).
- `SecurityUtils` is a Spring component that reads the current authenticated user from `SecurityContextHolder` — use `securityUtils.getCurrentUser()` / `getCurrentUserId()` inside services to get the acting user.

### Async & Scheduling

- `@Async` notifications run on a shared `ThreadPoolTaskExecutor` (core=4, max=10). `NotificationServiceImpl.createNotification()` is the main async entry point.
- Two schedulers run daily at 09:00: `DocumentExpiryScheduler` and `ContractExpiryScheduler`, which scan for items expiring within 30 days.

### Caching

Redis caching (TTL 1 h) is configured for lookup data and role permissions. Cache names used: `lookups`, `permissions`.

### Reporting

`ReportServiceImpl` produces `.xlsx` files via Apache POI. `ExcelUtils` in `util/` provides helpers for headers and cell styles. JasperReports is a dependency but not yet wired to any service.

### Configuration

All sensitive values are externalised as environment variables with development defaults in `application.yaml`:

| Variable                     | Default                                       |
| ---------------------------- | --------------------------------------------- |
| `JWT_SECRET`                 | `YourSuperSecretKeyThatIsAtLeast256BitsLong!` |
| `MINIO_URL`                  | `http://localhost:9000`                       |
| `MINIO_ACCESS_KEY`           | `admin`                                       |
| `MINIO_SECRET_KEY`           | `password123`                                 |
| `SPRING_DATA_REDIS_HOST`     | `localhost`                                   |
| `SPRING_DATA_REDIS_PASSWORD` | _(empty)_                                     |

The `docker-compose.yml` sets Redis password to `redis123` for the container but leaves `SPRING_DATA_REDIS_PASSWORD` empty in the backend service — align these if running full Docker stack.
