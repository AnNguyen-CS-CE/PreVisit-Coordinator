package com.previsitcoordinator.intake;

/**
 * The fixed set of non-diagnostic outcomes that this application accepts from a call provider.
 */
enum CallOutcome {
    COMPLETED,
    RECIPIENT_UNAVAILABLE,
    CALLBACK_REQUESTED
}
