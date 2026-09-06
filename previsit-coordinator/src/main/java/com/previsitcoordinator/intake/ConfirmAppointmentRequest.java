package com.previsitcoordinator.intake;

import java.util.UUID;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * Records an explicit confirmation for the exact previously proposed slot.
 */
public record ConfirmAppointmentRequest(
        @NotNull UUID slotId,
        @NotNull @AssertTrue Boolean confirmed) {
}
