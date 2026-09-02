package com.previsitcoordinator.intake;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data supplied by clinic staff when starting a demo intake case.
 */
record CreateIntakeCaseRequest(
        @NotBlank String patientReference,
        @NotBlank @Pattern(regexp = "\\+[1-9]\\d{7,14}") String demoPhoneNumber) {
}
