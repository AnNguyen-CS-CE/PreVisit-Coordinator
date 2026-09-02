package com.previsitcoordinator.intake;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(IntakeCaseController.class)
@Import(IntakeCaseService.class)
class IntakeCaseEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void startsAnIntakeCaseForValidDemoPatientData() throws Exception {
        mockMvc.perform(post("/api/intake-cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientReference": "demo-patient-001",
                                  "demoPhoneNumber": "+15551234567"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caseId").isNotEmpty())
                .andExpect(jsonPath("$.patientReference").value("demo-patient-001"))
                .andExpect(jsonPath("$.caseStatus").value("STAFF_STARTED"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void rejectsABlankPatientReference() throws Exception {
        mockMvc.perform(post("/api/intake-cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientReference": "   ",
                                  "demoPhoneNumber": "+15551234567"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsANonE164DemoPhoneNumber() throws Exception {
        mockMvc.perform(post("/api/intake-cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientReference": "demo-patient-001",
                                  "demoPhoneNumber": "555-123-4567"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
