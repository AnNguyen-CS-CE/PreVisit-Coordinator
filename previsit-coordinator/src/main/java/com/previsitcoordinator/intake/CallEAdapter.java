package com.previsitcoordinator.intake;

/**
 * The seam between intake coordination and CALL-E. Implementations may start and poll a provider
 * call, but they receive only an E.164 destination and the patient's preferred language.
 */
interface CallEAdapter {

    ProviderCallRun startNonMedicalCoordinationCall(String e164Recipient, String preferredLanguage);

    ProviderCallRun findCallRun(String providerRunId);
}
