# Biere - Beer Catalog

This project is a robust and scalable REST API for managing a beer catalog, built with Kotlin and Spring Boot. The project utilizes **Hexagonal Architecture (Ports and Adapters)** to ensure a business core decoupled from external technologies and follows the **Richardson Maturity Model** up to its highest level.

## 🏗️ Architecture

The project has been refactored to follow the principles of **Hexagonal Architecture**, focusing on dependency inversion and the protection of business rules.

### Layer Structure:
- **`domain`**: Contains the application "Core" (Models, Business Exceptions, and Port Interfaces). It has no dependencies on external frameworks.
- **`application`**: Contains the **Use Cases (Services)** that implement business rules and interact with the Ports.
- **`infrastructure`**: Contains the input **Adapters** (REST Controllers) and output (Persistence Adapters), as well as framework configurations (Spring Boot).

---

## 🚀 Best Practices Adopted

The API was developed following strict standards to ensure consistency, usability, and ease of integration:

### 1. HATEOAS (Hypermedia as the Engine of Application State)
We reached level 3 of the Richardson Model. API responses do not only contain data but also dynamic links that guide the client (e.g., `self`, `update_beer`, `delete_beer`).

### 2. Problem Details (RFC 9457)
Standardization of error responses using the `application/problem+json` format. Errors include `title`, `status`, `detail`, `instance`, `timestamp`, and `trace-id`, as well as HATEOAS links to assist in error recovery.

### 3. Content Negotiation
Support for multiple formats via the `Accept` header:
- `application/json`: Standard format.
- `application/hal+json`: Hypermedia Application Language.
- `text/csv`: Export of structured data.

### 4. Semantic Status Codes
Precise use of HTTP codes:
- `200 OK`, `201 Created`, `204 No Content` for success.
- `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, `409 Conflict`, `422 Unprocessable Entity` for client errors.

---

## 🛠️ Technologies Used

- **Language:** Kotlin 1.9+
- **Framework:** Spring Boot 3.4+
- **Database:** PostgreSQL
- **Persistence:** Spring Data JPA / Hibernate
- **Security:** Spring Security + JWT (JSON Web Token)
- **Documentation:** OpenAPI 3 / SpringDoc (Swagger)
- **Testing:** JUnit 5, MockK, Testcontainers (PostgreSQL)
- **HATEOAS:** Spring HATEOAS

---

## 📖 API Documentation

The API is organized into main resources: Beers, Breweries, Styles, Countries, and Users. For all write operations (POST, PUT, PATCH), the use of the `X-Idempotency-Key` header is recommended to avoid duplicate processing.

### 🔐 Authentication and Users
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/v1/users` | Registers a new user. |
| `POST` | `/v1/users/login` | Authenticates the user and returns tokens (Access and Refresh). |
| `POST` | `/v1/users/refresh-token` | Renews the Access Token using the Refresh Token. |
| `DELETE` | `/v1/users/{id}` | Removes a user account. |

#### Getting Started with Authentication
To interact with protected endpoints, you must obtain a **JSON Web Token (JWT)** and include it in the `Authorization` header of your requests as a Bearer token: `Authorization: Bearer <your_token>`.

**How to get the token:**
Perform a `POST` request to `/v1/users/login` with the user's credentials. The response will contain an `accessToken`.

**Demo Credentials:**
Since this is a demonstration application, the following credentials are provided for testing purposes:

*   **Administrator User** (Full permissions to all operations):
    *   **Email:** `admin@biere.com`
    *   **Password:** `fsdfs`
*   **Agent User** (Read-only access, can only view data):
    *   **Email:** `agent@biere.com`
    *   **Password:** `fsafadfdsfsfsdfsfsfefvakfoaca cdks`

> [!NOTE]
> These passwords are being shared publicly because this application is for demonstration and portfolio purposes only.


### 🍺 Beers
*Requires scopes: `beers:read` or `beers:write`.*
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/v1/beers` | Lists beers (paginated). Supports `Accept: text/csv`. |
| `POST` | `/v1/beers` | Registers a new beer. |
| `GET` | `/v1/beers/{id}` | Details of a specific beer. |
| `PATCH` | `/v1/beers/{id}` | Partial update of a beer. |
| `DELETE` | `/v1/beers/{id}` | Removes a beer. |

### 🏭 Breweries
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/v1/breweries` | Lists all breweries. |
| `POST` | `/v1/breweries` | Registers a new brewery. |
| `GET` | `/v1/breweries/{id}` | Details of a brewery. |
| `PUT` | `/v1/breweries/{id}` | Updates an existing brewery. |
| `DELETE` | `/v1/breweries/{id}` | Removes a brewery. |

### 🎨 Styles and Countries
Both follow the basic CRUD pattern to manage support entities:
- **Styles:** `/v1/styles`
- **Countries:** `/v1/countries`

For full interactive documentation (Swagger UI), start the application and access: `http://localhost:8080/swagger-ui.html`.

---

## 🤖 Agentic-Ready API

This API is designed to be **"Agentic-Ready"**, meaning it is optimized for autonomous consumption by AI agents and LLMs. Beyond traditional REST standards, we have implemented advanced concepts that ensure efficiency, security, and discoverability for machines:

### 1. Granular Scopes (OAuth2 Scopes)
- **Motivation:** AI agents operate with specific permissions. Instead of "Admin/User," we use scopes like `beers:read` and `beers:write`.
- **Benefit:** Principle of least privilege, ensuring an agent tasked only with reading cannot accidentally delete data.

### 2. Mandatory Idempotency (`X-Idempotency-Key`)
- **Motivation:** AI agents may experience timeouts or network failures and need to retry actions.
- **Implementation:** Required for all write methods (`POST`, `PUT`, `PATCH`).
- **Benefit:** Prevents the creation of duplicate resources (e.g., registering the same beer twice) during automatic retries.

### 3. Rate Limiting with `Retry-After`
- **Motivation:** Control costs (tokens) and server load.
- **Implementation:** `X-Rate-Limit-Limit`, `X-Rate-Limit-Remaining` headers, and the standard `Retry-After`.
- **Benefit:** The agent knows exactly how long to wait before trying again, avoiding aggressive blocking.

### 4. Conditional Caching (`ETags` and `If-None-Match`)
- **Motivation:** Reduce response body processing in polling loops.
- **Benefit:** If the data hasn't changed, the server returns `304 Not Modified`. This saves **bandwidth** and **processing tokens** for the AI agent, which does not need to re-read the JSON.

### 5. Resource Lifecycle (`Deprecation` and `Sunset`)
- **Motivation:** APIs evolve. Agents need to know when an endpoint will cease to exist.
- **Implementation:** RFC 8594 headers.
- **Benefit:** Agents can proactively detect obsolete versions and find the successor via the `rel="successor-version"` link.

### 6. Traceability (`X-Correlation-Id` / `X-Request-Id`)
- **Motivation:** Debugging multi-step flows performed by AI agents.
- **Benefit:** Correlates server logs with agent actions, facilitating auditing and troubleshooting in complex orchestrations.

### 7. Content Transparency (`Vary`, `Accept-Patch`, `Content-Location`)
- **Motivation:** Eliminate "guessing" by AI about supported formats and URIs.
- **Benefit:** The agent knows which PATCH formats are accepted and the canonical URI of the newly created resource without hallucinating.

---

## 🏗️ Richardson Maturity Model

This project incorporates all levels of the Richardson model, achieving the "**Glory of REST**":

- **Level 0:** Surpassing simple RPC calls.
- **Level 1 (Resources):** Individual URIs for each resource (e.g., `/v1/beers/{id}`).
- **Level 2 (HTTP Verbs):** Proper use of `GET`, `POST`, `PUT`, `PATCH`, and `DELETE`.
- **Level 3 (Hypermedia Controls):** Full implementation of **HATEOAS**.

---

## 🛠️ How to Run

1. **Requirements:** Docker and JDK 17+ installed.
2. **Setup:** The database is configured via Testcontainers in tests, but for manual execution, use a local or containerized PostgreSQL database.
3. **Execution:**
   ```bash
   ./gradlew bootRun
   ```
4. **Documentation:** Access `http://localhost:8080/swagger-ui.html` after starting the application.

---

## ✅ Project Status
- [x] Hexagonal Architecture (Ports & Adapters)
- [x] Authentication and Authorization with JWT
- [x] Full CRUD for Beers, Breweries, Styles, and Countries
- [x] Full HATEOAS integration
- [x] Standardized Errors (RFC 9457)
- [x] Integration Tests with Testcontainers
- [x] Caching Implementation (ETags)
- [x] AI Agent Support (Agentic-Ready)