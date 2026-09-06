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

    IntakeCaseResponse startCase(CreateIntakeCaseRequest request) {
        IntakeCase intakeCase = new IntakeCase(
                UUID.randomUUID(),
                request.patientReference(),
                request.demoPhoneNumber(),
                IntakeCaseStatus.STAFF_STARTED,
                Instant.now(),
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
                intakeSubmission);

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
                intakeCase.intakeSubmission());

        cases.put(caseId, approvedCase);

        return toResponse(approvedCase);
    }

    private IntakeCaseResponse toResponse(IntakeCase intakeCase) {
        return new IntakeCaseResponse(
                intakeCase.caseId(),
                intakeCase.patientReference(),
                intakeCase.caseStatus(),
                intakeCase.createdAt());
    }

    private ActiveStaffAlertResponse toActiveAlertResponse(StaffAlert staffAlert) {
        return new ActiveStaffAlertResponse(
                staffAlert.alertId(),
                staffAlert.caseId(),
                staffAlert.patientReference(),
                staffAlert.createdAt());
    }
}
