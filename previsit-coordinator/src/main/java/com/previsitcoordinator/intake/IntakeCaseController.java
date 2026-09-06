package com.previsitcoordinator.intake;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles HTTP actions for starting, retrieving, and submitting intake cases.
 */
@RestController
class IntakeCaseController {

    private final IntakeCaseService intakeCaseService;

    IntakeCaseController(IntakeCaseService intakeCaseService) {
        this.intakeCaseService = intakeCaseService;
    }

    @PostMapping("/api/intake-cases")
    @ResponseStatus(HttpStatus.CREATED)
    IntakeCaseResponse startCase(@Valid @RequestBody CreateIntakeCaseRequest request) {
        return intakeCaseService.startCase(request);
    }

    @GetMapping("/api/intake-cases/{caseId}")
    IntakeCaseResponse findCase(@PathVariable UUID caseId) {
        return intakeCaseService.findCase(caseId);
    }

    @PostMapping("/api/intake-cases/{caseId}/intake-submission")
    IntakeCaseResponse submitIntake(
            @PathVariable UUID caseId,
            @Valid @RequestBody SubmitIntakeRequest request) {
        return intakeCaseService.submitIntake(caseId, request);
    }

    @PostMapping("/api/intake-cases/{caseId}/scheduling-approval")
    IntakeCaseResponse approveScheduling(@PathVariable UUID caseId) {
        return intakeCaseService.approveScheduling(caseId);
    }

    @GetMapping("/api/intake-cases/{caseId}/available-slots")
    List<AppointmentSlot> findAvailableSlots(@PathVariable UUID caseId) {
        return intakeCaseService.findAvailableSlots(caseId);
    }

    @PostMapping("/api/intake-cases/{caseId}/slot-proposal")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void proposeSlot(
            @PathVariable UUID caseId,
            @Valid @RequestBody ProposeSlotRequest request) {
        intakeCaseService.proposeSlot(caseId, request);
    }

    @PostMapping("/api/intake-cases/{caseId}/appointment-confirmation")
    @ResponseStatus(HttpStatus.CREATED)
    Appointment confirmAppointment(
            @PathVariable UUID caseId,
            @Valid @RequestBody ConfirmAppointmentRequest request) {
        return intakeCaseService.confirmAppointment(caseId, request);
    }
}
