package com.previsitcoordinator.intake;

/**
 * Validated, non-diagnostic result data retained for a call run.
 */
record CallResult(
        CallOutcome outcome,
        boolean callbackRequested) {
}
