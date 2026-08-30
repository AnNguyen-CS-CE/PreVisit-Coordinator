package com.previsitcoordinator;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles HTTP requests that belong to the health-check API.
 *
 * <p>This controller is deliberately small: it does not contain CALL-E, patient,
 * or scheduling logic. Its job is to prove that a client can reach the backend.
 * {@code @RestController} tells Spring to send a method's return value directly
 * in the HTTP response body rather than looking for an HTML page.</p>
 */
@RestController
class HealthController {

    /**
     * Returns the public health contract for the backend.
     *
     * <p>{@code @GetMapping} connects an HTTP GET request for {@code /api/health}
     * to this Java method. Spring converts the returned map into JSON, so
     * {@code Map.of("status", "ok")} becomes {@code {"status":"ok"}}.</p>
     *
     * @return a small JSON-ready value that reports a healthy backend
     */
    @GetMapping("/api/health")
    Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
