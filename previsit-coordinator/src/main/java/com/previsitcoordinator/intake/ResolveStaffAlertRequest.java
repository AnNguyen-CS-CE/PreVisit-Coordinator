package com.previsitcoordinator.intake;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Non-diagnostic information staff provide when resolving an alert.
 */
record ResolveStaffAlertRequest(
        @NotNull AlertResolutionReason reason,
        @NotBlank String note) {
}
