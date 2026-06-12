# 📋 Habit Tracker REST API

A RESTful back-end application built with **Spring Boot 3.2.5** for tracking personal habits and daily completions. Implements Richardson Maturity Level 4 with full **HATEOAS** hypermedia support.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Data Access | Spring Data JPA / Hibernate |
| Database | H2 In-Memory |
| Hypermedia | Spring HATEOAS |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI 2.5.0 / Swagger UI |
| Build | Apache Maven |
| Testing | JUnit 5, MockMvc, BDD / Cucumber |

---

## 📦 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+

### Run locally

```bash
git clone https://github.com/juxtoposs/habit-tracker.git
cd habit-tracker
mvn spring-boot:run
```

The application starts on **http://localhost:8080**.

---

## 🔗 API Endpoints

### Habits

| Method | Path | Description | Status |
|---|---|---|---|
| `GET` | `/api/habits` | Get all habits | 200 |
| `GET` | `/api/habits/{id}` | Get habit by ID | 200 / 404 |
| `POST` | `/api/habits` | Create a new habit | 201 / 400 |
| `PUT` | `/api/habits/{id}` | Update a habit | 200 / 400 |
| `DELETE` | `/api/habits/{id}` | Delete a habit (cascades logs) | 204 |

### Habit Logs

| Method | Path | Description | Status |
|---|---|---|---|
| `GET` | `/api/habits/{id}/logs` | Get completion logs for a habit | 200 / 404 |
| `GET` | `/api/habits/{id}/logs/{logId}` | Get a single completion log by ID | 200 / 404 |
| `POST` | `/api/habits/{id}/logs` | Mark habit as completed (`?completedDate=yyyy-MM-dd`) | 201 / 400 / 404 / 409 |
| `DELETE` | `/api/habits/{id}/logs/{logId}` | Delete a log entry belonging to the selected habit | 204 / 404 |

### Example: Create a habit

```bash
curl -X POST http://localhost:8080/api/habits \
  -H "Content-Type: application/json" \
  -d '{"name": "Morning Run", "description": "5 km jog every morning"}'
```

Response:

```json
{
  "id": 1,
  "name": "Morning Run",
  "description": "5 km jog every morning",
  "createdAt": "2025-06-10T08:00:00",
  "_links": {
    "self":   { "href": "http://localhost:8080/api/habits/1" },
    "logs":   { "href": "http://localhost:8080/api/habits/1/logs" },
    "delete": { "href": "http://localhost:8080/api/habits/1" }
  }
}
```
---

## ⚡ Caching

Read-only API responses use short private HTTP caching with ETag support.

Example cache validation flow:
```bash
curl -i [http://localhost:8080/api/habits](http://localhost:8080/api/habits)
```
The response includes headers such as:
```http
ETag: "..." Cache-Control: max-age=30, private, must-revalidate
```

Clients can reuse the `ETag` value with `If-None-Match`. If the resource has not changed, the API returns `304 Not Modified`.


---

## 📐 Architecture

```
Controller  →  Service (interface)  →  ServiceImpl  →  Repository  →  H2
```

- **Controller** — handles HTTP, builds HATEOAS response models  
- **Service** — enforces business rules (duplicate check, validation)  
- **Repository** — Spring Data JPA; provides CRUD out of the box  
- **Model** — `Habit` and `HabitLog` JPA entities

---

## 🧪 Tests

- Integration tests for controller endpoints (`MockMvc` + embedded Spring context)
- Unit tests for service logic, DTO validation, entity lifecycle behavior, and API error handling
- BDD scenarios written in Gherkin / Cucumber for end-to-end API behavior


Run all tests:
```bash
mvn test
```
Test results: there should be **46 passed tests**

Current Cucumber scenarios cover:
- API root discovery through HATEOAS links
- Creating habits successfully
- Rejecting invalid habit input with field validation errors
- Retrieving all habits with collection links
- Marking a habit as completed on a specific date
- Preventing duplicate completion logs
- Preventing deletion of a completion log through the wrong habit URL
- Validating HTTP cache behavior with `ETag`, `Cache-Control`, and `304 Not Modified`

Cucumber reports are generated after test execution:
| Report | Path |
|---|---|
| HTML report | `target/cucumber-report.html` |
| JSON report | `target/cucumber-report.json` |


---

## 📖 API Documentation

After starting the app, open:

| Tool | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |
| H2 Console | http://localhost:8080/h2-console |

H2 console settings: JDBC URL `jdbc:h2:mem:habitdb`, user `sa`, no password.

---

## 📁 Project Structure

```
src/
├── main/java/com/example/habittracker/
│   ├── controller/        # HabitController, HabitLogController
│   ├── service/           # HabitService, HabitLogService (+ Impl)
│   ├── repository/        # HabitRepository, HabitLogRepository
│   ├── model/             # Habit, HabitLog entities
│   └── config/            # OpenApiConfig
└── test/
    ├── java/              # JUnit / MockMvc integration & unit tests
    └── resources/features # Cucumber .feature files (BDD scenarios)
```

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
