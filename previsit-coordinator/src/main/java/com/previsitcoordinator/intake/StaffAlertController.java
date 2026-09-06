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
 * Handles staff-alert retrieval for coordination work.
 */
@RestController
class StaffAlertController {

    private final IntakeCaseService intakeCaseService;

    StaffAlertController(IntakeCaseService intakeCaseService) {
        this.intakeCaseService = intakeCaseService;
    }

    @GetMapping("/api/staff-alerts")
    List<ActiveStaffAlertResponse> findActiveAlerts() {
        return intakeCaseService.findActiveAlerts();
    }

    @PostMapping("/api/staff-alerts/{alertId}/resolution")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void resolveAlert(
            @PathVariable UUID alertId,
            @Valid @RequestBody ResolveStaffAlertRequest request) {
        intakeCaseService.resolveStaffAlert(alertId, request);
    }
}
