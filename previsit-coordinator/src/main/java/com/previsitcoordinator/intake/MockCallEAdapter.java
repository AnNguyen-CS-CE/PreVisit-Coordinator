package com.previsitcoordinator.intake;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * Local adapter used until a CALL-E provider-backed adapter is configured. It never places a call.
 */
@Component
class MockCallEAdapter implements CallEAdapter {

    private final Map<String, ProviderCallRun> providerRuns = new ConcurrentHashMap<>();

    @Override
    public ProviderCallRun startNonMedicalCoordinationCall(String e164Recipient, String preferredLanguage) {
        String providerRunId = "mock-calle-" + UUID.randomUUID();
        ProviderCallRun providerCallRun = new ProviderCallRun(
                providerRunId,
                CallRunStatus.COMPLETED,
                new CallResult(CallOutcome.COMPLETED, false));
        providerRuns.put(providerRunId, providerCallRun);
        return providerCallRun;
    }

    @Override
    public ProviderCallRun findCallRun(String providerRunId) {
        ProviderCallRun providerCallRun = providerRuns.get(providerRunId);
        if (providerCallRun == null) {
            throw new IllegalArgumentException("Provider call run not found");
        }
        return providerCallRun;
    }
}
