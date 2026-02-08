package com.inductiveautomation.mtconnect.gateway.connection;

public record ConnectionDeviceInfo(
        String id,
        String name,
        String uuid,
        String description
) {
}
