package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.UUID;

/**
 * Internal record that links one intake case to a provider-side call run.
 */
record CallRun(
        UUID runId,
        UUID caseId,
        String providerRunId,
        CallRunStatus callRunStatus,
        CallResult result,
        Instant createdAt,
        Instant updatedAt) {
}
