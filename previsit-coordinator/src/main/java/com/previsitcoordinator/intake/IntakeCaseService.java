package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Owns intake-case state, including case creation, retrieval, and submission.
 */
@Service
class IntakeCaseService {

    private final Map<UUID, IntakeCase> cases = new ConcurrentHashMap<>();
    private final Map<UUID, StaffAlert> staffAlerts = new ConcurrentHashMap<>();
    private final Map<UUID, CallRun> callRuns = new ConcurrentHashMap<>();
    private final AppointmentGateway appointmentGateway;
    private final CallEAdapter callEAdapter;

    IntakeCaseService(AppointmentGateway appointmentGateway, CallEAdapter callEAdapter) {
        this.appointmentGateway = appointmentGateway;
        this.callEAdapter = callEAdapter;
    }

    IntakeCaseResponse startCase(CreateIntakeCaseRequest request) {
        IntakeCase intakeCase = new IntakeCase(
                UUID.randomUUID(),
                request.patientReference(),
                request.demoPhoneNumber(),
                IntakeCaseStatus.STAFF_STARTED,
                Instant.now(),
                null,
                null,
                null);

        cases.put(intakeCase.caseId(), intakeCase);

        return toResponse(intakeCase);
    }

    IntakeCaseResponse findCase(UUID caseId) {
        IntakeCase intakeCase = cases.get(caseId);
        if (intakeCase == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Intake case not found");
        }

        return toResponse(intakeCase);
    }

    synchronized IntakeCaseResponse submitIntake(UUID caseId, SubmitIntakeRequest request) {
        IntakeCase intakeCase = cases.get(caseId);
        if (intakeCase == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Intake case not found");
        }
        if (intakeCase.intakeSubmission() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Intake already submitted");
        }
        IntakeSubmission intakeSubmission = new IntakeSubmission(
                request.reasonForVisit(),
                request.preferredLanguage(),
                request.mobilityAssistanceNeeded(),
                request.emergencyFlag());
        IntakeCaseStatus caseStatus = intakeSubmission.emergencyFlag()
                ? IntakeCaseStatus.SCHEDULING_HALTED
                : IntakeCaseStatus.INTAKE_COMPLETE;
        IntakeCase submittedCase = new IntakeCase(
                intakeCase.caseId(),
                intakeCase.patientReference(),
                intakeCase.demoPhoneNumber(),
                caseStatus,
                intakeCase.createdAt(),
                intakeSubmission,
                intakeCase.proposedSlot(),
                intakeCase.appointment());

        cases.put(caseId, submittedCase);
        if (intakeSubmission.emergencyFlag()) {
            StaffAlert staffAlert = new StaffAlert(
                    UUID.randomUUID(),
                    submittedCase.caseId(),
                    submittedCase.patientReference(),
                    Instant.now(),
                    null);
            staffAlerts.put(staffAlert.alertId(), staffAlert);
        }

        return toResponse(submittedCase);
    }

    List<ActiveStaffAlertResponse> findActiveAlerts() {
        return staffAlerts.values().stream()
                .filter(staffAlert -> staffAlert.alertResolution() == null)
                .sorted(Comparator.comparing(StaffAlert::createdAt))
                .map(this::toActiveAlertResponse)
                .toList();
    }

    synchronized void resolveStaffAlert(UUID alertId, ResolveStaffAlertRequest request) {
        StaffAlert staffAlert = staffAlerts.get(alertId);
        if (staffAlert == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff alert not found");
        }
        if (staffAlert.alertResolution() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Staff alert already resolved");
        }

        AlertResolution alertResolution = new AlertResolution(
                request.reason(),
                request.note(),
                Instant.now());
        StaffAlert resolvedAlert = new StaffAlert(
                staffAlert.alertId(),
                staffAlert.caseId(),
                staffAlert.patientReference(),
                staffAlert.createdAt(),
                alertResolution);

        staffAlerts.put(alertId, resolvedAlert);
    }

    synchronized IntakeCaseResponse approveScheduling(UUID caseId) {
        IntakeCase intakeCase = cases.get(caseId);
        if (intakeCase == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Intake case not found");
        }
        if (intakeCase.caseStatus() != IntakeCaseStatus.INTAKE_COMPLETE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Intake case cannot be approved for scheduling");
        }

        IntakeCase approvedCase = new IntakeCase(
                intakeCase.caseId(),
                intakeCase.patientReference(),
                intakeCase.demoPhoneNumber(),
                IntakeCaseStatus.SCHEDULING_APPROVED,
                intakeCase.createdAt(),
                intakeCase.intakeSubmission(),
                intakeCase.proposedSlot(),
                intakeCase.appointment());

        cases.put(caseId, approvedCase);

        return toResponse(approvedCase);
    }

    synchronized CallRunResponse startCallRun(UUID caseId) {
        IntakeCase intakeCase = findIntakeCase(caseId);
        if (intakeCase.caseStatus() != IntakeCaseStatus.INTAKE_COMPLETE
                && intakeCase.caseStatus() != IntakeCaseStatus.SCHEDULING_APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Intake case cannot start a coordination call");
        }

        ProviderCallRun providerCallRun = callEAdapter.startNonMedicalCoordinationCall(
                intakeCase.demoPhoneNumber(),
                intakeCase.intakeSubmission().preferredLanguage());
        Instant now = Instant.now();
        CallRun callRun = new CallRun(
                UUID.randomUUID(),
                intakeCase.caseId(),
                providerCallRun.providerRunId(),
                providerCallRun.callRunStatus(),
                providerCallRun.result(),
                now,
                now);
        callRuns.put(callRun.runId(), callRun);
        return toCallRunResponse(callRun);
    }

    synchronized CallRunResponse findCallRun(UUID caseId, UUID runId) {
        findIntakeCase(caseId);
        CallRun callRun = callRuns.get(runId);
        if (callRun == null || !callRun.caseId().equals(caseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Call run not found");
        }

        ProviderCallRun providerCallRun = callEAdapter.findCallRun(callRun.providerRunId());
        CallRun refreshedCallRun = new CallRun(
                callRun.runId(),
                callRun.caseId(),
                callRun.providerRunId(),
                providerCallRun.callRunStatus(),
                providerCallRun.result(),
                callRun.createdAt(),
                Instant.now());
        callRuns.put(runId, refreshedCallRun);
        return toCallRunResponse(refreshedCallRun);
    }

    List<AppointmentSlot> findAvailableSlots(UUID caseId) {
        IntakeCase intakeCase = cases.get(caseId);
        if (intakeCase == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Intake case not found");
        }
        if (intakeCase.caseStatus() != IntakeCaseStatus.SCHEDULING_APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Intake case cannot view available slots");
        }

        return appointmentGateway.findAvailableSlots(intakeCase);
    }

    synchronized void proposeSlot(UUID caseId, ProposeSlotRequest request) {
        IntakeCase intakeCase = cases.get(caseId);
        if (intakeCase == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Intake case not found");
        }
        if (intakeCase.caseStatus() != IntakeCaseStatus.SCHEDULING_APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Intake case cannot propose an appointment slot");
        }
        AppointmentSlot proposedSlot = appointmentGateway.findAvailableSlots(intakeCase).stream()
                .filter(slot -> slot.slotId().equals(request.slotId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Appointment slot is not available"));

        IntakeCase caseWithProposedSlot = new IntakeCase(
                intakeCase.caseId(),
                intakeCase.patientReference(),
                intakeCase.demoPhoneNumber(),
                intakeCase.caseStatus(),
                intakeCase.createdAt(),
                intakeCase.intakeSubmission(),
                proposedSlot,
                intakeCase.appointment());
        cases.put(caseId, caseWithProposedSlot);
    }

    synchronized Appointment confirmAppointment(UUID caseId, ConfirmAppointmentRequest request) {
        IntakeCase intakeCase = cases.get(caseId);
        if (intakeCase == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Intake case not found");
        }
        if (intakeCase.caseStatus() != IntakeCaseStatus.SCHEDULING_APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Intake case cannot confirm an appointment");
        }
        if (intakeCase.appointment() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment already scheduled");
        }
        if (intakeCase.proposedSlot() == null || !intakeCase.proposedSlot().slotId().equals(request.slotId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment confirmation must match the proposed slot");
        }

        Appointment appointment = appointmentGateway.bookConfirmedAppointment(intakeCase, intakeCase.proposedSlot());
        IntakeCase caseWithAppointment = new IntakeCase(
                intakeCase.caseId(),
                intakeCase.patientReference(),
                intakeCase.demoPhoneNumber(),
                intakeCase.caseStatus(),
                intakeCase.createdAt(),
                intakeCase.intakeSubmission(),
                intakeCase.proposedSlot(),
                appointment);
        cases.put(caseId, caseWithAppointment);

        return appointment;
    }

    private IntakeCaseResponse toResponse(IntakeCase intakeCase) {
        return new IntakeCaseResponse(
                intakeCase.caseId(),
                intakeCase.patientReference(),
                intakeCase.caseStatus(),
                intakeCase.createdAt());
    }

    private IntakeCase findIntakeCase(UUID caseId) {
        IntakeCase intakeCase = cases.get(caseId);
        if (intakeCase == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Intake case not found");
        }
        return intakeCase;
    }

    private CallRunResponse toCallRunResponse(CallRun callRun) {
        return new CallRunResponse(
                callRun.runId(),
                callRun.callRunStatus(),
                callRun.result(),
                callRun.createdAt(),
                callRun.updatedAt());
    }

    private ActiveStaffAlertResponse toActiveAlertResponse(StaffAlert staffAlert) {
        return new ActiveStaffAlertResponse(
                staffAlert.alertId(),
                staffAlert.caseId(),
                staffAlert.patientReference(),
                staffAlert.createdAt());
    }
}
