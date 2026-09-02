package com.previsitcoordinator.intake;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * Starts intake cases and owns the rules for their initial server-generated state.
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

        return new IntakeCaseResponse(
                intakeCase.caseId(),
                intakeCase.patientReference(),
                intakeCase.caseStatus(),
                intakeCase.createdAt());
    }
}
