package com.previsitcoordinator.intake;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({IntakeCaseController.class, StaffAlertController.class})
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
    void retrievesAnExistingIntakeCase() throws Exception {
        String caseId = startCaseAndGetId();

        mockMvc.perform(get("/api/intake-cases/{caseId}", caseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseId").value(caseId))
                .andExpect(jsonPath("$.patientReference").value("demo-patient-001"))
                .andExpect(jsonPath("$.caseStatus").value("STAFF_STARTED"));
    }

    @Test
    void submitsANonEmergencyIntakeAndMarksCaseIntakeComplete() throws Exception {
        String caseId = startCaseAndGetId();

        mockMvc.perform(post("/api/intake-cases/{caseId}/intake-submission", caseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reasonForVisit": "Routine follow-up",
                                  "preferredLanguage": "English",
                                  "mobilityAssistanceNeeded": false,
                                  "emergencyFlag": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseId").value(caseId))
                .andExpect(jsonPath("$.patientReference").value("demo-patient-001"))
                .andExpect(jsonPath("$.caseStatus").value("INTAKE_COMPLETE"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void approvesAnIntakeCompleteCaseForScheduling() throws Exception {
        String caseId = startCaseAndGetId();

        submitNonEmergencyIntake(caseId);

        mockMvc.perform(post("/api/intake-cases/{caseId}/scheduling-approval", caseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseId").value(caseId))
                .andExpect(jsonPath("$.caseStatus").value("SCHEDULING_APPROVED"));
    }

    @Test
    void refusesSchedulingApprovalBeforeIntakeIsComplete() throws Exception {
        String caseId = startCaseAndGetId();

        mockMvc.perform(post("/api/intake-cases/{caseId}/scheduling-approval", caseId))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/intake-cases/{caseId}", caseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseStatus").value("STAFF_STARTED"));
    }

    @Test
    void refusesASecondSchedulingApprovalForTheSameCase() throws Exception {
        String caseId = startCaseAndGetId();

        submitNonEmergencyIntake(caseId);

        mockMvc.perform(post("/api/intake-cases/{caseId}/scheduling-approval", caseId))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/intake-cases/{caseId}/scheduling-approval", caseId))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/intake-cases/{caseId}", caseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseStatus").value("SCHEDULING_APPROVED"));
    }

    @Test
    void returnsNotFoundWhenApprovingAnUnknownIntakeCase() throws Exception {
        mockMvc.perform(post(
                        "/api/intake-cases/{caseId}/scheduling-approval",
                        "8bb6d5a3-4e6e-4f1a-b407-30f4556254c5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitsAnEmergencyFlaggedIntakeAndHaltsScheduling() throws Exception {
        String caseId = startCaseAndGetId();

        mockMvc.perform(post("/api/intake-cases/{caseId}/intake-submission", caseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reasonForVisit": "Immediate concern",
                                  "preferredLanguage": "English",
                                  "mobilityAssistanceNeeded": true,
                                  "emergencyFlag": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseStatus").value("SCHEDULING_HALTED"));

        mockMvc.perform(get("/api/intake-cases/{caseId}", caseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseStatus").value("SCHEDULING_HALTED"));
    }

    @Test
    void listsAnActiveStaffAlertForAnEmergencyFlaggedIntake() throws Exception {
        String caseId = startCaseAndGetId();

        submitEmergencyIntake(caseId);

        String activeAlertsResponse = mockMvc.perform(get("/api/staff-alerts"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Matcher alertMatcher = Pattern.compile(
                        "\\{\\\"alertId\\\":\\\"[^\\\"]+\\\",\\\"caseId\\\":\\\"" + caseId
                                + "\\\",\\\"patientReference\\\":\\\"demo-patient-001\\\",\\\"createdAt\\\":\\\"[^\\\"]+\\\"\\}")
                .matcher(activeAlertsResponse);
        assertTrue(alertMatcher.find());
        assertFalse(activeAlertsResponse.contains("\"demoPhoneNumber\""));
    }

    @Test
    void resolvesAnAlertWithoutRemovingTheRelatedSchedulingHalt() throws Exception {
        String caseId = startCaseAndGetId();
        submitEmergencyIntake(caseId);
        String alertId = findActiveAlertIdForCase(caseId);

        mockMvc.perform(post("/api/staff-alerts/{alertId}/resolution", alertId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reason": "CARE_FOLLOW_UP_RECORDED",
                                  "note": "Staff follow-up recorded."
                                }
                                """))
                .andExpect(status().isNoContent());

        String activeAlertsResponse = mockMvc.perform(get("/api/staff-alerts"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertFalse(activeAlertsResponse.contains("\"alertId\":\"" + alertId + "\""));

        mockMvc.perform(get("/api/intake-cases/{caseId}", caseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseStatus").value("SCHEDULING_HALTED"));

        mockMvc.perform(post("/api/intake-cases/{caseId}/scheduling-approval", caseId))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsAnAlertResolutionWithoutAReason() throws Exception {
        mockMvc.perform(post(
                        "/api/staff-alerts/{alertId}/resolution",
                        "8bb6d5a3-4e6e-4f1a-b407-30f4556254c5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "note": "Staff follow-up recorded."
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAnAlertResolutionWithABlankNote() throws Exception {
        mockMvc.perform(post(
                        "/api/staff-alerts/{alertId}/resolution",
                        "8bb6d5a3-4e6e-4f1a-b407-30f4556254c5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reason": "OTHER",
                                  "note": "   "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundWhenResolvingAnUnknownStaffAlert() throws Exception {
        mockMvc.perform(post(
                        "/api/staff-alerts/{alertId}/resolution",
                        "8bb6d5a3-4e6e-4f1a-b407-30f4556254c5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reason": "OTHER",
                                  "note": "Staff follow-up recorded."
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void refusesSchedulingApprovalForASchedulingHaltedCase() throws Exception {
        String caseId = startCaseAndGetId();

        mockMvc.perform(post("/api/intake-cases/{caseId}/intake-submission", caseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reasonForVisit": "Immediate concern",
                                  "preferredLanguage": "English",
                                  "mobilityAssistanceNeeded": true,
                                  "emergencyFlag": true
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/intake-cases/{caseId}/scheduling-approval", caseId))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/intake-cases/{caseId}", caseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.caseStatus").value("SCHEDULING_HALTED"));
    }

    @Test
    void rejectsAnIntakeSubmissionWithABlankReasonForVisit() throws Exception {
        mockMvc.perform(post(
                        "/api/intake-cases/{caseId}/intake-submission",
                        "8bb6d5a3-4e6e-4f1a-b407-30f4556254c5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reasonForVisit": "   ",
                                  "preferredLanguage": "English",
                                  "mobilityAssistanceNeeded": false,
                                  "emergencyFlag": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAnIntakeSubmissionWithABlankPreferredLanguage() throws Exception {
        mockMvc.perform(post(
                        "/api/intake-cases/{caseId}/intake-submission",
                        "8bb6d5a3-4e6e-4f1a-b407-30f4556254c5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reasonForVisit": "Routine follow-up",
                                  "preferredLanguage": "   ",
                                  "mobilityAssistanceNeeded": false,
                                  "emergencyFlag": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAnIntakeSubmissionWithoutMobilityAssistanceNeeded() throws Exception {
        mockMvc.perform(post(
                        "/api/intake-cases/{caseId}/intake-submission",
                        "8bb6d5a3-4e6e-4f1a-b407-30f4556254c5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reasonForVisit": "Routine follow-up",
                                  "preferredLanguage": "English",
                                  "emergencyFlag": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAnIntakeSubmissionWithoutAnEmergencyFlag() throws Exception {
        mockMvc.perform(post(
                        "/api/intake-cases/{caseId}/intake-submission",
                        "8bb6d5a3-4e6e-4f1a-b407-30f4556254c5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reasonForVisit": "Routine follow-up",
                                  "preferredLanguage": "English",
                                  "mobilityAssistanceNeeded": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsASecondIntakeSubmissionForTheSameCase() throws Exception {
        String caseId = startCaseAndGetId();

        String intakeSubmission = """
                {
                  "reasonForVisit": "Routine follow-up",
                  "preferredLanguage": "English",
                  "mobilityAssistanceNeeded": false,
                  "emergencyFlag": false
                }
                """;

        mockMvc.perform(post("/api/intake-cases/{caseId}/intake-submission", caseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(intakeSubmission))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/intake-cases/{caseId}/intake-submission", caseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(intakeSubmission))
                .andExpect(status().isConflict());
    }

    @Test
    void returnsNotFoundForAnUnknownIntakeCase() throws Exception {
        mockMvc.perform(get("/api/intake-cases/{caseId}", "8bb6d5a3-4e6e-4f1a-b407-30f4556254c5"))
                .andExpect(status().isNotFound());
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

    private String startCaseAndGetId() throws Exception {
        String createdCaseResponse = mockMvc.perform(post("/api/intake-cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "patientReference": "demo-patient-001",
                                  "demoPhoneNumber": "+15551234567"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Matcher caseIdMatcher = Pattern.compile("\\\"caseId\\\":\\\"([^\\\"]+)\\\"")
                .matcher(createdCaseResponse);
        assertTrue(caseIdMatcher.find());

        return caseIdMatcher.group(1);
    }

    private void submitNonEmergencyIntake(String caseId) throws Exception {
        mockMvc.perform(post("/api/intake-cases/{caseId}/intake-submission", caseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reasonForVisit": "Routine follow-up",
                                  "preferredLanguage": "English",
                                  "mobilityAssistanceNeeded": false,
                                  "emergencyFlag": false
                                }
                                """))
                .andExpect(status().isOk());
    }

    private void submitEmergencyIntake(String caseId) throws Exception {
        mockMvc.perform(post("/api/intake-cases/{caseId}/intake-submission", caseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "reasonForVisit": "Immediate concern",
                                  "preferredLanguage": "English",
                                  "mobilityAssistanceNeeded": true,
                                  "emergencyFlag": true
                                }
                                """))
                .andExpect(status().isOk());
    }

    private String findActiveAlertIdForCase(String caseId) throws Exception {
        String activeAlertsResponse = mockMvc.perform(get("/api/staff-alerts"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Matcher alertIdMatcher = Pattern.compile(
                        "\\{\\\"alertId\\\":\\\"([^\\\"]+)\\\",\\\"caseId\\\":\\\"" + caseId + "\\\"")
                .matcher(activeAlertsResponse);
        assertTrue(alertIdMatcher.find());

        return alertIdMatcher.group(1);
    }
}
