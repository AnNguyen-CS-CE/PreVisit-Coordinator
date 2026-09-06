package com.previsitcoordinator.intake;

import java.time.Instant;
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

        return toResponse(submittedCase);
    }

    private IntakeCaseResponse toResponse(IntakeCase intakeCase) {
        return new IntakeCaseResponse(
                intakeCase.caseId(),
                intakeCase.patientReference(),
                intakeCase.caseStatus(),
                intakeCase.createdAt());
    }
}
