package com.previsitcoordinator.intake;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * Identifies the one offered slot a patient is considering.
 */
public record ProposeSlotRequest(@NotNull UUID slotId) {
}
