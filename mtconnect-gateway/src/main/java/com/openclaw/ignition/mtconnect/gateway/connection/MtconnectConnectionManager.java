package com.openclaw.ignition.mtconnect.gateway.connection;

import com.inductiveautomation.ignition.gateway.config.DecodedResource;
import com.inductiveautomation.ignition.gateway.config.NamedResourceHandler;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.openclaw.ignition.mtconnect.common.client.HttpMtconnectClient;
import com.openclaw.ignition.mtconnect.gateway.config.MtconnectConnectionResource;
import com.openclaw.ignition.mtconnect.gateway.polling.MtconnectPollingService;
import com.openclaw.ignition.mtconnect.common.model.MtconnectDevices;
import com.openclaw.ignition.mtconnect.common.model.MtconnectDevice;
import com.inductiveautomation.ignition.common.resourcecollection.PushException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MtconnectConnectionManager {
    private static final String TASK_OWNER = MtconnectConnectionManager.class.getName();
    private static final String TASK_NAME = "mtconnect-connection-sync";
    private static final int SYNC_INTERVAL_MS = 5000;

    private final Logger logger = LoggerFactory.getLogger(MtconnectConnectionManager.class);
    private final GatewayContext context;
    private final NamedResourceHandler<MtconnectConnectionResource> handler;
    private final Map<String, MtconnectPollingService> services = new HashMap<>();
    private final Map<String, MtconnectConnectionResource> configs = new HashMap<>();

    public MtconnectConnectionManager(
            GatewayContext context,
            NamedResourceHandler<MtconnectConnectionResource> handler
    ) {
        this.context = context;
        this.handler = handler;
    }

    public void startup() {
        syncResources();
        context.getExecutionManager().register(TASK_OWNER, TASK_NAME, this::syncResources, SYNC_INTERVAL_MS);
    }

    public void shutdown() {
        context.getExecutionManager().unRegister(TASK_OWNER, TASK_NAME);
        stopAll();
    }

    public List<ConnectionStatus> getStatusSnapshot() {
        List<ConnectionStatus> snapshot = new ArrayList<>();
        for (Map.Entry<String, MtconnectConnectionResource> entry : configs.entrySet()) {
            String name = entry.getKey();
            MtconnectConnectionResource config = entry.getValue();
            MtconnectPollingService service = services.get(name);
            snapshot.add(new ConnectionStatus(
                    name,
                    config.agentUrl(),
                    config.deviceName(),
                    config.pollIntervalMs(),
                    service == null ? null : service.getLastPollTime(),
                    service == null ? "Not running" : service.getLastError(),
                    service != null && service.isRunning(),
                    service != null && service.isConnected(),
                    service == null ? 0 : service.getUptimeSeconds(),
                    service == null ? 0 : service.getBackoffUntilEpochMs(),
                    service == null ? 0 : service.getBackoffRemainingSeconds(),
                    service == null ? 0 : service.getFailureCount(),
                    service == null ? null : service.getLastSuccessTime(),
                    service == null ? 0 : service.getTotalTagCount(),
                    service == null ? 0 : service.getLastObservedTagCount()
            ));
        }
        return snapshot;
    }

    public List<ConnectionConfigSnapshot> listConnectionConfigs() {
        List<ConnectionConfigSnapshot> snapshot = new ArrayList<>();
        for (DecodedResource<MtconnectConnectionResource> resource : handler.getResources()) {
            String name = resource.name();
            MtconnectConnectionResource config = resource.config();
            if (name == null || name.isBlank() || config == null) {
                continue;
            }
            snapshot.add(new ConnectionConfigSnapshot(
                    name,
                    config.agentUrl(),
                    config.deviceName(),
                    config.pollIntervalMs(),
                    config.tagProviderName()
            ));
        }
        return snapshot;
    }

    public ConnectionTestResult testConnection(String name) {
        if (name == null || name.isBlank()) {
            return new ConnectionTestResult(null, false, "Missing connection name", Instant.now().toString());
        }
        MtconnectConnectionResource config = configs.get(name);
        if (config == null) {
            return new ConnectionTestResult(name, false, "Connection not found", Instant.now().toString());
        }
        try {
            HttpMtconnectClient client = new HttpMtconnectClient(config.agentUrl());
            client.probe(config.deviceName());
            return new ConnectionTestResult(name, true, "Probe succeeded", Instant.now().toString());
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message == null || message.isBlank()) {
                message = ex.getClass().getSimpleName();
            }
            return new ConnectionTestResult(name, false, message, Instant.now().toString());
        }
    }

    public ConnectionDevicesResult probeDevices(String name) {
        if (name == null || name.isBlank()) {
            return new ConnectionDevicesResult(null, false, "Missing connection name", Instant.now().toString(), List.of());
        }
        MtconnectConnectionResource config = configs.get(name);
        if (config == null) {
            return new ConnectionDevicesResult(name, false, "Connection not found", Instant.now().toString(), List.of());
        }
        try {
            HttpMtconnectClient client = new HttpMtconnectClient(config.agentUrl());
            MtconnectDevices devices = client.probe(null);
            List<ConnectionDeviceInfo> deviceInfo = new ArrayList<>();
            for (MtconnectDevice device : devices.getDevices()) {
                deviceInfo.add(new ConnectionDeviceInfo(
                        device.getId(),
                        device.getName(),
                        device.getUuid(),
                        device.getDescription()
                ));
            }
            return new ConnectionDevicesResult(name, true, "Probe succeeded", Instant.now().toString(), deviceInfo);
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message == null || message.isBlank()) {
                message = ex.getClass().getSimpleName();
            }
            return new ConnectionDevicesResult(name, false, message, Instant.now().toString(), List.of());
        }
    }

    public ConnectionUpdateResult updateDeviceName(String name, String deviceName) {
        if (name == null || name.isBlank()) {
            return new ConnectionUpdateResult(null, false, "Missing connection name", Instant.now().toString());
        }
        MtconnectConnectionResource config = configs.get(name);
        if (config == null) {
            return new ConnectionUpdateResult(name, false, "Connection not found", Instant.now().toString());
        }
        String sanitizedDevice = (deviceName == null || deviceName.isBlank()) ? null : deviceName;
        MtconnectConnectionResource updated = new MtconnectConnectionResource(
                config.agentUrl(),
                sanitizedDevice,
                config.pollIntervalMs(),
                config.tagProviderName()
        );
        try {
            handler.modify(name, updated).join();
            syncResources();
            return new ConnectionUpdateResult(name, true, "Device updated", Instant.now().toString());
        } catch (Exception ex) {
            Throwable cause = ex instanceof PushException ? ex : ex.getCause();
            String message = cause == null ? ex.getMessage() : cause.getMessage();
            if (message == null || message.isBlank()) {
                message = ex.getClass().getSimpleName();
            }
            return new ConnectionUpdateResult(name, false, message, Instant.now().toString());
        }
    }

    public ConnectionUpdateResult createConnection(String name, MtconnectConnectionResource config) {
        if (name == null || name.isBlank()) {
            return new ConnectionUpdateResult(null, false, "Missing connection name", Instant.now().toString());
        }
        if (config == null) {
            return new ConnectionUpdateResult(name, false, "Missing configuration", Instant.now().toString());
        }
        try {
            handler.create(name, config).join();
            syncResources();
            return new ConnectionUpdateResult(name, true, "Connection created", Instant.now().toString());
        } catch (Exception ex) {
            return buildUpdateError(name, ex);
        }
    }

    public ConnectionUpdateResult updateConnection(String name, MtconnectConnectionResource config) {
        if (name == null || name.isBlank()) {
            return new ConnectionUpdateResult(null, false, "Missing connection name", Instant.now().toString());
        }
        if (config == null) {
            return new ConnectionUpdateResult(name, false, "Missing configuration", Instant.now().toString());
        }
        try {
            handler.modify(name, config).join();
            syncResources();
            return new ConnectionUpdateResult(name, true, "Connection updated", Instant.now().toString());
        } catch (Exception ex) {
            return buildUpdateError(name, ex);
        }
    }

    public ConnectionUpdateResult deleteConnection(String name) {
        if (name == null || name.isBlank()) {
            return new ConnectionUpdateResult(null, false, "Missing connection name", Instant.now().toString());
        }
        try {
            handler.delete(name).join();
            syncResources();
            return new ConnectionUpdateResult(name, true, "Connection deleted", Instant.now().toString());
        } catch (Exception ex) {
            return buildUpdateError(name, ex);
        }
    }

    private ConnectionUpdateResult buildUpdateError(String name, Exception ex) {
        Throwable cause = ex instanceof PushException ? ex : ex.getCause();
        String message = cause == null ? ex.getMessage() : cause.getMessage();
        if (message == null || message.isBlank()) {
            message = ex.getClass().getSimpleName();
        }
        return new ConnectionUpdateResult(name, false, message, Instant.now().toString());
    }

    private void syncResources() {
        List<DecodedResource<MtconnectConnectionResource>> resources = handler.getResources();
        Set<String> seen = new HashSet<>();
        for (DecodedResource<MtconnectConnectionResource> resource : resources) {
            String name = resource.name();
            MtconnectConnectionResource config = resource.config();
            if (name == null || name.isBlank() || config == null) {
                continue;
            }
            seen.add(name);
            MtconnectConnectionResource previous = configs.get(name);
            if (previous == null) {
                startService(name, config);
            } else if (!previous.equals(config)) {
                restartService(name, config);
            }
        }
        Set<String> toRemove = new HashSet<>(services.keySet());
        toRemove.removeAll(seen);
        for (String name : toRemove) {
            stopService(name);
        }
    }

    private void startService(String name, MtconnectConnectionResource config) {
        try {
            MtconnectPollingService service = new MtconnectPollingService(context, name, config);
            service.start();
            services.put(name, service);
            configs.put(name, config);
            logger.info("Started MTConnect connection {}", name);
        } catch (Exception ex) {
            logger.warn("Failed to start MTConnect connection {}", name, ex);
        }
    }

    private void restartService(String name, MtconnectConnectionResource config) {
        stopService(name);
        startService(name, config);
    }

    private void stopService(String name) {
        MtconnectPollingService service = services.remove(name);
        configs.remove(name);
        if (service == null) {
            return;
        }
        try {
            service.stop();
            logger.info("Stopped MTConnect connection {}", name);
        } catch (Exception ex) {
            logger.warn("Failed to stop MTConnect connection {}", name, ex);
        }
    }

    private void stopAll() {
        for (String name : new HashSet<>(services.keySet())) {
            stopService(name);
        }
    }
}
