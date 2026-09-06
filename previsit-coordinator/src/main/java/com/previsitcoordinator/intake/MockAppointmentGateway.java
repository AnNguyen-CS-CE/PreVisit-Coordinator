package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * Fixed local availability for the demo while no external appointment provider is configured.
 */
@Component
class MockAppointmentGateway implements AppointmentGateway {

    private static final List<AppointmentSlot> AVAILABLE_SLOTS = List.of(
            new AppointmentSlot(
                    UUID.fromString("42c5ee1d-93e2-465b-905f-cc632031ed05"),
                    Instant.parse("2026-09-14T14:00:00Z"),
                    Instant.parse("2026-09-14T14:30:00Z"),
                    "Dr. Avery Chen",
                    "Demo Primary Care Clinic",
                    "Primary care visit"),
            new AppointmentSlot(
                    UUID.fromString("047cd0ee-4a4b-4d8c-80c8-3fbe7abec4a2"),
                    Instant.parse("2026-09-15T16:00:00Z"),
                    Instant.parse("2026-09-15T16:30:00Z"),
                    "Dr. Avery Chen",
                    "Demo Primary Care Clinic",
                    "Primary care visit"));

    @Override
    public List<AppointmentSlot> findAvailableSlots(IntakeCase intakeCase) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public Appointment bookConfirmedAppointment(IntakeCase intakeCase, AppointmentSlot slot) {
        return new Appointment(
                UUID.randomUUID(),
                slot,
                intakeCase.patientReference(),
                AppointmentStatus.SCHEDULED);
    }
}
