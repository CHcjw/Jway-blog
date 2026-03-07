# MySQL Setup

1. Execute schema:

```sql
source backend/sql/mysql_schema.sql;
```

2. Add dependency in `pom.xml` (when using MySQL runtime):

```xml
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <scope>runtime</scope>
</dependency>
```

3. Start backend with MySQL profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

4. Env vars (optional):
- `MYSQL_HOST`
- `MYSQL_PORT`
- `MYSQL_DB`
- `MYSQL_USER`
- `MYSQL_PASSWORD`

Admin env vars (required):
- `ADMIN_API_PREFIX`
- `ADMIN_ENTRY_KEY`
- `ADMIN_USERNAME`
- `ADMIN_PASSWORD_HASH` (recommended)
- `ADMIN_PASSWORD` (optional fallback)
