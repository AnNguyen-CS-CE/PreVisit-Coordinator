package com.previsitcoordinator.intake;

import java.util.UUID;

/**
 * A scheduled appointment created after explicit confirmation.
 */
public record Appointment(
        UUID appointmentId,
        AppointmentSlot slot,
        String patientReference,
        AppointmentStatus status) {
}
