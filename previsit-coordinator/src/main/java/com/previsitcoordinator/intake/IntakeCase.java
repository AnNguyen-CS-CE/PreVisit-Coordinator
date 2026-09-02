package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.UUID;

/**
 * Internal representation of one intake case held by this demo application.
 */
record IntakeCase(
        UUID caseId,
        String patientReference,
        String demoPhoneNumber,
        IntakeCaseStatus caseStatus,
        Instant createdAt) {
}
