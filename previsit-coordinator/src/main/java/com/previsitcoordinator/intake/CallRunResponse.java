package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.UUID;

/**
 * Staff-safe representation of a call run. Provider identifiers and raw call text stay internal.
 */
record CallRunResponse(
        UUID runId,
        CallRunStatus callRunStatus,
        CallResult result,
        Instant createdAt,
        Instant updatedAt) {
}
