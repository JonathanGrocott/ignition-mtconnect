package com.openclaw.ignition.mtconnect.gateway.connection;

public record ConnectionUpdateResult(
        String name,
        boolean success,
        String message,
        String timestamp
) {
}
