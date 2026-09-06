package com.previsitcoordinator.intake;

/**
 * Internal non-diagnostic answers submitted once for an intake case.
 */
record IntakeSubmission(
        String reasonForVisit,
        String preferredLanguage,
        boolean mobilityAssistanceNeeded,
        boolean emergencyFlag) {
}
