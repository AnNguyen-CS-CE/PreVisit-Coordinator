# 1. Health endpoint: request to JSON

- **Original file covered:** [HealthController.java](../../previsit-coordinator/src/main/java/com/previsitcoordinator/HealthController.java), `health`
- **Responsibility:** Handle a health-check request and return a small JSON response.
- **Understand first:** [Lesson 02](02-application-entry-and-component-scanning.md): the main class and Spring's component discovery.

# 2. Heavily Commented Code

```java
package com.previsitcoordinator;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Register this class as a REST controller: its method values become HTTP bodies.
@RestController
class HealthController {

    // Route an HTTP GET request for this exact path to the method below.
    @GetMapping("/api/health")
    Map<String, String> health() {
        // Create an immutable map with one text key and one text value.
        // Spring serializes this returned data as JSON for the caller.
        return Map.of("status", "ok");
    }
}
```

# 3. Deep Dive Analysis

## What is it?

An **HTTP GET request** asks a server to return information. A **controller** is a class that receives HTTP requests. `@GetMapping` associates a URL path with one method. `Map<String, String>` means a collection of key/value pairs where both key and value are text. `Map.of(...)` creates a small immutable map: after creation, its contents cannot be changed. **JSON** is a text data format commonly used between a frontend and backend.

## Why use it here?

The project needs a simple, public signal that the backend can respond. The stable key `status` gives a future dashboard or deployment check a predictable field to inspect. A map is enough because there is only one named value. Creating a Patient or Appointment class here would be invented complexity; those concepts are not implemented yet.

## When to use it?

Use `@GetMapping` for a read-style HTTP endpoint. Use a map for a tiny, flexible response with only a few fields. When the response grows into an important, stable domain shape—such as an intake case—prefer a named Java type rather than an anonymous map. Do not use a health check to expose private patient or system data.

## How to use it?

The generic types in `Map<String, String>` prevent placing non-text values into this map. `Map.of("status", "ok")` uses alternating key and value arguments. A frequent beginner mistake is reversing their meaning and returning `Map.of("ok", "status")`; Java accepts it, but API clients receive the wrong contract. Spring writes a REST controller's return value to the response through its HTTP message conversion system; see the [Spring controller return-value reference](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/return-types.html).

# 4. Feynman Technique Explanation

Think of the controller as a clinic reception desk. The sign `@GetMapping("/api/health")` says, “People asking for this exact service come to this desk.” When someone arrives with `GET /api/health`, the receptionist runs `health()`. The receptionist returns a small labeled card: the label is `status`, and its answer is `ok`. Spring translates that card into the JSON language that a browser or frontend understands.

# 5. Realistic Example

Current-project request and response:

```http
GET /api/health

HTTP/1.1 200 OK
Content-Type: application/json

{"status":"ok"}
```

This is the external result of the existing `health()` method. The client does not see Java's `Map`; it sees JSON after Spring handles the response. The exact HTTP header can depend on the running server, but the status and JSON structure are the contract this project tests.

# 6. Your Turn: Proof of Understanding

Assume a client requests `GET /api/health`.

1. Which method runs, and why?
2. What JSON structure does the current map represent?
3. If the code returned `Map.of("status", "maintenance")`, what would change for the client?

Answer in sentences; do not write new code yet.
