package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Owns intake-case state, including case creation and retrieval.
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
                Instant.now());

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

    private IntakeCaseResponse toResponse(IntakeCase intakeCase) {
        return new IntakeCaseResponse(
                intakeCase.caseId(),
                intakeCase.patientReference(),
                intakeCase.caseStatus(),
                intakeCase.createdAt());
    }
}
