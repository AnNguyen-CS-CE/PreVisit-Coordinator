package com.previsitcoordinator.intake;

import java.time.Instant;

/**
 * Internal staff-recorded resolution for a staff alert.
 */
record AlertResolution(
        AlertResolutionReason reason,
        String note,
        Instant resolvedAt) {
}
