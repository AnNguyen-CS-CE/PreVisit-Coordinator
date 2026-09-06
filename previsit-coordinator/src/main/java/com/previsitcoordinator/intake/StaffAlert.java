package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.UUID;

/**
 * Internal durable coordination record created for an emergency-flagged intake case.
 */
record StaffAlert(
        UUID alertId,
        UUID caseId,
        String patientReference,
        Instant createdAt,
        AlertResolution alertResolution) {
}
