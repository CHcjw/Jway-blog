# Jway Blog Backend

Spring Boot backend for Jway Blog mock replacement.

## Run

```bash
cd backend
mvn spring-boot:run
```

Before starting, set admin environment variables:

- `ADMIN_API_PREFIX` (example: `/api/secret-cms-x9`)
- `ADMIN_ENTRY_KEY`
- `ADMIN_USERNAME`
- `ADMIN_PASSWORD_HASH` (recommended bcrypt hash)
- `ADMIN_PASSWORD` (optional fallback, not recommended)

## Profiles

- `dev` (default): H2 in-memory + seed data
- `mysql`: MySQL connection (see `backend/sql/README.md`)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## API Base

- `http://localhost:8080/api`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Admin API is hidden and configurable: `${ADMIN_API_PREFIX}`

## Endpoints

- `GET /api/posts?page=1&size=9`
- `GET /api/posts/{id}`
- `GET /api/posts/latest?limit=5`
- `GET /api/posts/related/{id}?limit=4`
- `GET /api/posts/search?keyword=&category=&tag=&page=1&size=9`
- `GET /api/categories`
- `GET /api/tags`
- `GET /api/works`
- `GET /api/skills`
- `GET /api/moments`
- `GET /api/statistics`

All responses follow:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```
