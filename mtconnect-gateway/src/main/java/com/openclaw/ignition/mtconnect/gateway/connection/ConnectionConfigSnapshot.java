package com.openclaw.ignition.mtconnect.gateway.connection;

public record ConnectionConfigSnapshot(
        String name,
        String agentUrl,
        String deviceName,
        Integer pollIntervalMs,
        String tagProviderName
) {
}
