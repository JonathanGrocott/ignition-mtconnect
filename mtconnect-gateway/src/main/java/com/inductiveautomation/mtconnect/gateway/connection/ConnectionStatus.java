package com.inductiveautomation.mtconnect.gateway.connection;

public record ConnectionStatus(
        String name,
        String agentUrl,
        String deviceName,
        Integer pollIntervalMs,
        String lastPollTime,
        String lastError,
        boolean running,
        boolean connected,
        long uptimeSeconds,
        long backoffUntilEpochMs,
        long backoffRemainingSeconds,
        int failureCount,
        String lastSuccessTime,
        int totalTagCount,
        int lastObservedTagCount
) {
}
