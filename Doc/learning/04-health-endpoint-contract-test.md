# 1. Health endpoint contract test

- **Original file covered:** [HealthEndpointTest.java](../../previsit-coordinator/src/test/java/com/previsitcoordinator/HealthEndpointTest.java), `reportsThatTheBackendIsHealthy`
- **Responsibility:** Verify the observable HTTP contract of the health endpoint without starting a real network server.
- **Understand first:** [Lesson 03](03-health-endpoint-request-to-json.md): the request path, controller method, map, and JSON response.

# 2. Heavily Commented Code

```java
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

// Create Spring's focused MVC test environment rather than a full live server.
@WebMvcTest
class HealthEndpointTest {

    // Spring supplies an in-memory request/response testing tool to this field.
    @Autowired
    private MockMvc mockMvc;

    // JUnit runs this method as one test case.
    @Test
    void reportsThatTheBackendIsHealthy() throws Exception {
        // Send a simulated HTTP GET request through Spring MVC.
        mockMvc.perform(get("/api/health"))
                // Check the endpoint reports successful HTTP status 200.
                .andExpect(status().isOk())
                // Check the response contains the expected JSON structure.
                .andExpect(content().json("{\"status\":\"ok\"}"));
    }
}
```

# 3. Deep Dive Analysis

## What is it?

A **test** is code that checks another piece of code automatically. `@Test` marks a method for JUnit to run. `MockMvc` is Spring's test tool for sending mock HTTP requests through Spring MVC and checking the response. `@Autowired` asks Spring to provide a required object. The chain beginning with `mockMvc.perform(...)` is a fluent sequence: each `.andExpect(...)` adds one requirement.

## Why use it here?

The health endpoint has two observable promises: it responds successfully and reports the `status` value. Testing through HTTP-level request handling protects the public contract instead of testing the controller method as an ordinary Java call. If a later edit accidentally changes the URL, status code, or response data, this test should fail before the change reaches a user.

## When to use it?

Use an MVC test when you need confidence in request routing, status codes, and response bodies but do not need a real network port, database, or outside service. Use a full end-to-end HTTP test separately when you need to prove that a running server can bind a port and receive network traffic. Do not confuse the two: this test is faster, but it does not prove the machine's network setup.

## How to use it?

`get("/api/health")` creates the request. `perform(...)` sends it into Spring MVC. `status().isOk()` expects HTTP 200. `content().json(...)` checks the expected JSON structure; it is not a strict check that no extra fields exist. A common beginner mistake is testing only that the method returns a map. That misses the URL mapping and JSON conversion that real clients depend on. Read the official [MockMvc reference](https://docs.spring.io/spring/reference/testing/mockmvc.html) for its boundary and capabilities.

# 4. Feynman Technique Explanation

Imagine a training patient walking to the clinic desk. `MockMvc` is that training patient: it follows the same reception process but does not need to travel through a real road or phone line. The patient asks for `/api/health`; the test checks the desk says “success” (HTTP 200) and hands back a card with `status: ok`. This proves the desk procedure works, but it does not prove the road to the clinic is open—that requires a real server check.

# 5. Realistic Example

Current-project test trace:

```text
mockMvc.perform(get("/api/health"))
  -> Spring routes the request to HealthController.health()
  -> the controller returns Map.of("status", "ok")
  -> the test expects HTTP 200 and the status/ok JSON structure
```

This trace uses only the real endpoint, controller, map, and assertions in this repository. It shows why the test is stronger than directly calling `health()`: routing and response conversion are part of the check.

# 6. Your Turn: Proof of Understanding

Explain why this test needs both assertions:

```java
.andExpect(status().isOk())
.andExpect(content().json("{\"status\":\"ok\"}"));
```

Then answer this: could the test pass if the application cannot bind port 8080 on a real computer? Explain why.
