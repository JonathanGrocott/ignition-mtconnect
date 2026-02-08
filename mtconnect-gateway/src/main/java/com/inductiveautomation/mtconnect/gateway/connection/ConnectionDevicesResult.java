package com.inductiveautomation.mtconnect.gateway.connection;

import java.util.List;

public record ConnectionDevicesResult(
        String name,
        boolean success,
        String message,
        String timestamp,
        List<ConnectionDeviceInfo> devices
) {
    public ConnectionDevicesResult {
        if (devices == null) {
            devices = List.of();
        }
    }
}
