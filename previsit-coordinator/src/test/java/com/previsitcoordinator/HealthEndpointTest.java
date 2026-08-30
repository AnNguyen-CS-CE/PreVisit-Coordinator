package com.previsitcoordinator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests the HTTP layer without starting the full production server.
 *
 * <p>{@code @WebMvcTest} creates Spring's web-testing environment and supplies
 * MockMvc. MockMvc lets this test send a request to the controller in memory,
 * which keeps the test fast and checks the public HTTP contract.</p>
 */
@WebMvcTest
class HealthEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * A health check is useful only if clients can rely on both its success status
     * and its response shape, so this test checks HTTP 200 and the expected JSON
     * structure.
     */
    @Test
    void reportsThatTheBackendIsHealthy() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"ok\"}"));
    }
}
