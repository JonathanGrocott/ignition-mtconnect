package com.inductiveautomation.mtconnect.gateway.connection;

public record ConnectionUpdateResult(
        String name,
        boolean success,
        String message,
        String timestamp
) {
}
