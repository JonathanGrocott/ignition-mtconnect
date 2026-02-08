package com.inductiveautomation.mtconnect.gateway.connection;

public record ConnectionTestResult(
        String name,
        boolean success,
        String message,
        String timestamp
) {
}
