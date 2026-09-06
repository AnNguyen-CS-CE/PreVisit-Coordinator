package com.previsitcoordinator.intake;

/**
 * Provider-facing state translated by the CALL-E adapter before it reaches the intake workflow.
 */
record ProviderCallRun(
        String providerRunId,
        CallRunStatus callRunStatus,
        CallResult result) {
}
