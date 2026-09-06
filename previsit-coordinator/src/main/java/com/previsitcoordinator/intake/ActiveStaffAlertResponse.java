package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.UUID;

/**
 * Public, non-diagnostic view of a staff alert that still needs attention.
 */
record ActiveStaffAlertResponse(
        UUID alertId,
        UUID caseId,
        String patientReference,
        Instant createdAt) {
}
