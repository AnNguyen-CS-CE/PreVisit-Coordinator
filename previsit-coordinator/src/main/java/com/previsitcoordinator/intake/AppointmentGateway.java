package com.previsitcoordinator.intake;

import java.util.List;

/**
 * Finds appointment availability without exposing a particular scheduling system to the workflow.
 */
interface AppointmentGateway {

    List<AppointmentSlot> findAvailableSlots(IntakeCase intakeCase);

    Appointment bookConfirmedAppointment(IntakeCase intakeCase, AppointmentSlot slot);
}
