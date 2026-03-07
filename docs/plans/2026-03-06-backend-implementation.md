# Jway Blog Backend Implementation Plan

> For Codex: Execute with TDD discipline (write failing tests first where practical).

**Goal:** Build a Spring Boot backend that replaces all frontend mock data with RESTful APIs.

**Architecture:** Layered architecture with `controller -> service -> repository -> entity`, plus DTO mapper and a unified `ApiResponse<T>` wrapper. Use H2 for runnable local demo with MySQL-compatible schema design.

**Tech Stack:** Java 17, Spring Boot 3, Spring Data JPA, H2, Validation, Springdoc, JUnit + MockMvc.

---

## Tasks

1. Create backend skeleton (`pom.xml`, application bootstrap, config, CORS, OpenAPI).
2. Create entities and repositories for posts/categories/tags/works/skills/moments.
3. Add seed data and schema initialization.
4. Add DTOs and mapper layer for API contracts.
5. Write controller tests for core endpoints.
6. Implement services and controllers to make tests pass.
7. Run tests and package verification.
