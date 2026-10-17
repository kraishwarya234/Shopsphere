# ShopSphere

Full-stack e-commerce platform built with **Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA/Hibernate, SQL Server, React (Vite) and Docker**.

## Features
- JWT authentication with BCrypt password hashing and role-based access (USER / ADMIN)
- Product catalog with search, category filter, sorting and pagination (JPA Specifications)
- Server-side shopping cart with stock validation
- Transactional order placement (stock deduction, cart clearing) and stock restoration on cancellation
- Admin panel: manage products, view all orders, update order status
- Global exception handling, request validation (DTOs), Swagger/OpenAPI docs
- Unit tests (JUnit 5, Mockito), Dockerfile, docker-compose, GitHub Actions CI

## Architecture
React SPA -> REST API (Controller -> Service -> Repository) -> SQL Server

## Run with Docker (backend + database)
docker compose up --build

- API: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui.html

Then start the frontend in a second terminal:
cd frontend && npm install && npm run dev
Open http://localhost:5173


## Run locally
1. Start SQL Server and create the database: `CREATE DATABASE shopsphere;`
2. Edit `backend/src/main/resources/application.properties` or set `DB_HOST`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`.
3. Backend: `cd backend && mvn spring-boot:run`
4. Frontend: `cd frontend && npm install && npm run dev` (http://localhost:5173)
   Note: `mvn spring-boot:run` serves the API on port 8080. Docker serves it on 8081. If you run locally with Maven, set the proxy in `frontend/vite.config.js` to `http://localhost:8080`.

Seeded admin account: `admin@shopsphere.com` / `Admin@123` (change before deploying).

## API summary
| Method | Endpoint | Access |
|---|---|---|
| POST | /api/auth/register, /api/auth/login | Public |
| GET | /api/products?q=&category=&minPrice=&maxPrice=&page=&size=&sort= | Public |
| POST/PUT/DELETE | /api/products, /api/products/{id} | Admin |
| GET/POST | /api/cart, /api/cart/items | User |
| PUT/DELETE | /api/cart/items/{id} | User |
| POST/GET | /api/orders | User |
| GET | /api/admin/orders | Admin |
| PATCH | /api/admin/orders/{id}/status | Admin |

## Tests
`cd backend && mvn test`
