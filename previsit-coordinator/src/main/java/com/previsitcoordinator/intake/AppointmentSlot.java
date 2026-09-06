package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.UUID;

/**
 * One exact time offered by the scheduling system.
 */
public record AppointmentSlot(
        UUID slotId,
        Instant startsAt,
        Instant endsAt,
        String providerName,
        String locationName,
        String serviceType) {
}
