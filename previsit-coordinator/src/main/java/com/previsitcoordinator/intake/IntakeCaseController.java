package com.previsitcoordinator.intake;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles the HTTP action for staff starting an intake case.
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
}
