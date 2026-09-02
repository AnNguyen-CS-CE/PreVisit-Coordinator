# PreVisit Coordinator — Project Learning Map

## Main purpose

This repository is starting a clinic pre-visit coordination backend. At the current stage, its only implemented behavior is a health check: a client asks `GET /api/health`, and the backend answers with `{"status":"ok"}`. That small feature is intentional. It proves the Java/Spring Boot foundation before the project adds patient, CALL-E, appointment, or dashboard logic.

## Important source files

| File | Current responsibility | Study after |
| --- | --- | --- |
| [pom.xml](../../previsit-coordinator/pom.xml) | Tells Maven how to build and test the application and which Spring libraries it needs. | Nothing |
| [PrevisitCoordinatorApplication.java](../../previsit-coordinator/src/main/java/com/previsitcoordinator/PrevisitCoordinatorApplication.java) | Java entry point; starts Spring Boot. | Maven setup |
| [HealthController.java](../../previsit-coordinator/src/main/java/com/previsitcoordinator/HealthController.java) | Connects `GET /api/health` to Java code and returns response data. | Application startup |
| [HealthEndpointTest.java](../../previsit-coordinator/src/test/java/com/previsitcoordinator/HealthEndpointTest.java) | Checks the endpoint's observable HTTP contract. | Endpoint behavior |

## Logical phases and edge cases

1. **Build setup:** Maven reads `pom.xml`, resolves the Spring libraries, and uses the conventional `src/main/java` and `src/test/java` folders.
2. **Application startup:** the Java Virtual Machine calls `main`; `SpringApplication.run(...)` prepares the Spring application.
3. **HTTP request handling:** Spring routes `GET /api/health` to `health()` and serializes its returned `Map` as JSON.
4. **Contract testing:** `MockMvc` checks an HTTP 200 response and the expected JSON structure without opening a real network port.

Current meaningful limitation: the test proves Spring MVC request handling, but it does not prove that a real machine can bind a port or that a browser can reach it. That later check must be run separately.

## Teaching boundary

The supplied lesson template asks for a separate “Realistic Example,” while its strict scope rule prohibits invented project code. This project has only one implemented endpoint, so each lesson uses a trace of that existing code instead of fictional classes, URLs, dependencies, or features.

## Study order

Read and complete one lesson at a time. Do not skip the “Your Turn” section.

1. [01-maven-project-setup.md](01-maven-project-setup.md)
2. [02-application-entry-and-component-scanning.md](02-application-entry-and-component-scanning.md)
3. [03-health-endpoint-request-to-json.md](03-health-endpoint-request-to-json.md)
4. [04-health-endpoint-contract-test.md](04-health-endpoint-contract-test.md)
