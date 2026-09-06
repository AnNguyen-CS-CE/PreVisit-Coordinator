package com.previsitcoordinator.intake;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Non-diagnostic information submitted by staff for an existing intake case.
 */
record SubmitIntakeRequest(
        @NotBlank String reasonForVisit,
        @NotBlank String preferredLanguage,
        @NotNull Boolean mobilityAssistanceNeeded,
        @NotNull Boolean emergencyFlag) {
}
