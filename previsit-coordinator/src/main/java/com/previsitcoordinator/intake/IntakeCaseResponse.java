package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.UUID;

/**
 * Public data returned to callers about an intake case.
 */
record IntakeCaseResponse(
        UUID caseId,
        String patientReference,
        IntakeCaseStatus caseStatus,
        Instant createdAt) {
}
