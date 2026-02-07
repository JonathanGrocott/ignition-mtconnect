package com.openclaw.ignition.mtconnect.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.gson.JsonArray;
import com.inductiveautomation.ignition.common.gson.JsonObject;
import com.inductiveautomation.ignition.gateway.config.ExtensionPoint;
import com.inductiveautomation.ignition.gateway.config.NamedResourceHandler;
import com.inductiveautomation.ignition.gateway.dataroutes.RouteGroup;
import com.inductiveautomation.ignition.gateway.dataroutes.HttpMethod;
import com.inductiveautomation.ignition.gateway.dataroutes.RequestContext;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.web.systemjs.SystemJsModule;
import com.inductiveautomation.ignition.gateway.web.session.WebUiSession;
import com.openclaw.ignition.mtconnect.gateway.connection.ConnectionStatus;
import com.openclaw.ignition.mtconnect.gateway.connection.ConnectionDevicesResult;
import com.openclaw.ignition.mtconnect.gateway.connection.ConnectionDeviceInfo;
import com.openclaw.ignition.mtconnect.gateway.connection.ConnectionTestResult;
import com.openclaw.ignition.mtconnect.gateway.connection.ConnectionUpdateResult;
import com.openclaw.ignition.mtconnect.gateway.connection.ConnectionConfigSnapshot;
import com.openclaw.ignition.mtconnect.gateway.connection.MtconnectConnectionManager;
import com.openclaw.ignition.mtconnect.gateway.config.MtconnectConnectionExtensionPoint;
import com.openclaw.ignition.mtconnect.gateway.config.MtconnectConnectionResource;
import java.util.List;
import java.util.Optional;

public class GatewayHook extends AbstractGatewayModuleHook {
    private NamedResourceHandler<MtconnectConnectionResource> connectionHandler;
    private MtconnectConnectionManager connectionManager;

    @Override
    public void setup(GatewayContext context) {
        connectionHandler = NamedResourceHandler.newBuilder(MtconnectConnectionResource.META)
                .context(context)
                .build();
        connectionManager = new MtconnectConnectionManager(context, connectionHandler);

        SystemJsModule jsModule = new SystemJsModule(
            "com.inductiveautomation.mtconnect.gateway",
            "/res/mtconnect/mtconnect-status.js"
        );

        context.getWebResourceManager().getNavigationModel().getConnections()
            .addCategory("mtconnect", category -> category
                .label("MTConnect")
                .addPage("Connections", page -> page
                    .position(5)
                    .mount("/mtconnect-connections", "MtconnectConnections", jsModule)
                )
                .addPage("Status", page -> page
                    .position(10)
                    .mount("/mtconnect-status", "MtconnectStatus", jsModule)
                )
            );
    }

    @Override
    public void startup(LicenseState licenseState) {
        if (connectionHandler != null) {
            connectionHandler.startup();
        }
        if (connectionManager != null) {
            connectionManager.startup();
        }
    }

    @Override
    public void shutdown() {
        if (connectionManager != null) {
            connectionManager.shutdown();
        }
        if (connectionHandler != null) {
            connectionHandler.shutdown();
        }
    }

    @Override
    public Optional<String> getMountedResourceFolder() {
        return Optional.of("mounted");
    }

    @Override
    public Optional<String> getMountPathAlias() {
        return Optional.of("mtconnect");
    }

    @Override
    public void mountRouteHandlers(RouteGroup routes) {
        routes.newRoute("/status")
                .type(RouteGroup.TYPE_JSON)
            .accessControl(WebUiSession.SESSION_READ)
                .handler((req, res) -> buildStatusResponse(req))
                .mount();
        routes.newRoute("/test")
            .type(RouteGroup.TYPE_JSON)
            .accessControl(WebUiSession.SESSION_READ)
            .handler((req, res) -> buildTestResponse(req.getRequest().getParameter("name")))
            .mount();
        routes.newRoute("/devices")
            .type(RouteGroup.TYPE_JSON)
            .accessControl(WebUiSession.SESSION_READ)
            .handler((req, res) -> buildDevicesResponse(req.getRequest().getParameter("name")))
            .mount();
        routes.newRoute("/set-device")
            .method(HttpMethod.POST)
            .type(RouteGroup.TYPE_JSON)
            .accessControl(WebUiSession.SESSION_WRITE)
            .handler((req, res) -> buildSetDeviceResponse(
                req.getParameter("name"),
                req.getParameter("device")
            ))
            .mount();
        routes.newRoute("/connections")
            .type(RouteGroup.TYPE_JSON)
            .accessControl(WebUiSession.SESSION_READ)
            .handler((req, res) -> buildConnectionsResponse(req))
            .mount();
        routes.newRoute("/connections/create")
            .method(HttpMethod.POST)
            .type(RouteGroup.TYPE_JSON)
            .accessControl(WebUiSession.SESSION_WRITE)
            .handler((req, res) -> buildConnectionCreateResponse(req))
            .mount();
        routes.newRoute("/connections/update")
            .method(HttpMethod.POST)
            .type(RouteGroup.TYPE_JSON)
            .accessControl(WebUiSession.SESSION_WRITE)
            .handler((req, res) -> buildConnectionUpdateResponse(req))
            .mount();
        routes.newRoute("/connections/delete")
            .method(HttpMethod.POST)
            .type(RouteGroup.TYPE_JSON)
            .accessControl(WebUiSession.SESSION_WRITE)
            .handler((req, res) -> buildConnectionDeleteResponse(req))
            .mount();
    }

    private JsonObject buildStatusResponse(RequestContext context) {
        JsonArray connections = new JsonArray();
        if (connectionManager != null) {
            for (ConnectionStatus status : connectionManager.getStatusSnapshot()) {
                JsonObject item = new JsonObject();
                item.addProperty("name", status.name());
                item.addProperty("agentUrl", status.agentUrl());
                item.addProperty("deviceName", status.deviceName());
                item.addProperty("pollIntervalMs", status.pollIntervalMs());
                item.addProperty("lastPollTime", status.lastPollTime());
                item.addProperty("lastError", status.lastError());
                item.addProperty("running", status.running());
                item.addProperty("connected", status.connected());
                item.addProperty("uptimeSeconds", status.uptimeSeconds());
                item.addProperty("backoffUntilEpochMs", status.backoffUntilEpochMs());
                item.addProperty("backoffRemainingSeconds", status.backoffRemainingSeconds());
                item.addProperty("failureCount", status.failureCount());
                item.addProperty("lastSuccessTime", status.lastSuccessTime());
                item.addProperty("totalTagCount", status.totalTagCount());
                item.addProperty("lastObservedTagCount", status.lastObservedTagCount());
                connections.add(item);
            }
        }
        JsonObject response = new JsonObject();
        WebUiSession.find(context).ifPresent(session ->
            response.addProperty("csrfToken", session.getCsrfToken())
        );
        response.add("connections", connections);
        return response;
    }

    private JsonObject buildTestResponse(String name) {
        ConnectionTestResult result = connectionManager == null
                ? new ConnectionTestResult(name, false, "Connection manager not available", null)
                : connectionManager.testConnection(name);
        JsonObject response = new JsonObject();
        response.addProperty("name", result.name());
        response.addProperty("success", result.success());
        response.addProperty("message", result.message());
        response.addProperty("timestamp", result.timestamp());
        return response;
    }

    private JsonObject buildDevicesResponse(String name) {
        ConnectionDevicesResult result = connectionManager == null
                ? new ConnectionDevicesResult(name, false, "Connection manager not available", null, List.of())
                : connectionManager.probeDevices(name);
        JsonArray devices = new JsonArray();
        for (ConnectionDeviceInfo device : result.devices()) {
            JsonObject item = new JsonObject();
            item.addProperty("id", device.id());
            item.addProperty("name", device.name());
            item.addProperty("uuid", device.uuid());
            item.addProperty("description", device.description());
            devices.add(item);
        }
        JsonObject response = new JsonObject();
        response.addProperty("name", result.name());
        response.addProperty("success", result.success());
        response.addProperty("message", result.message());
        response.addProperty("timestamp", result.timestamp());
        response.add("devices", devices);
        return response;
    }

    private JsonObject buildSetDeviceResponse(String name, String deviceName) {
        ConnectionUpdateResult result = connectionManager == null
                ? new ConnectionUpdateResult(name, false, "Connection manager not available", null)
                : connectionManager.updateDeviceName(name, deviceName);
        JsonObject response = new JsonObject();
        response.addProperty("name", result.name());
        response.addProperty("success", result.success());
        response.addProperty("message", result.message());
        response.addProperty("timestamp", result.timestamp());
        return response;
    }

    private JsonObject buildConnectionsResponse(RequestContext context) {
        JsonArray connections = new JsonArray();
        if (connectionManager != null) {
            for (ConnectionConfigSnapshot config : connectionManager.listConnectionConfigs()) {
                JsonObject item = new JsonObject();
                item.addProperty("name", config.name());
                item.addProperty("agentUrl", config.agentUrl());
                item.addProperty("deviceName", config.deviceName());
                item.addProperty("pollIntervalMs", config.pollIntervalMs());
                item.addProperty("tagProviderName", config.tagProviderName());
                connections.add(item);
            }
        }
        JsonObject response = new JsonObject();
        WebUiSession.find(context).ifPresent(session ->
            response.addProperty("csrfToken", session.getCsrfToken())
        );
        response.add("connections", connections);
        return response;
    }

    private JsonObject buildConnectionCreateResponse(RequestContext context) {
        String name = context.getParameter("name");
        MtconnectConnectionResource config = buildConnectionConfig(context);
        ConnectionUpdateResult result = connectionManager == null
                ? new ConnectionUpdateResult(name, false, "Connection manager not available", null)
                : connectionManager.createConnection(name, config);
        return buildUpdateResultJson(result);
    }

    private JsonObject buildConnectionUpdateResponse(RequestContext context) {
        String name = context.getParameter("name");
        MtconnectConnectionResource config = buildConnectionConfig(context);
        ConnectionUpdateResult result = connectionManager == null
                ? new ConnectionUpdateResult(name, false, "Connection manager not available", null)
                : connectionManager.updateConnection(name, config);
        return buildUpdateResultJson(result);
    }

    private JsonObject buildConnectionDeleteResponse(RequestContext context) {
        String name = context.getParameter("name");
        ConnectionUpdateResult result = connectionManager == null
                ? new ConnectionUpdateResult(name, false, "Connection manager not available", null)
                : connectionManager.deleteConnection(name);
        return buildUpdateResultJson(result);
    }

    private MtconnectConnectionResource buildConnectionConfig(RequestContext context) {
        String agentUrl = context.getParameter("agentUrl");
        String deviceName = context.getParameter("deviceName");
        String tagProviderName = context.getParameter("tagProviderName");
        Integer pollIntervalMs = parsePollInterval(context.getParameter("pollIntervalMs"));
        return new MtconnectConnectionResource(agentUrl, deviceName, pollIntervalMs, tagProviderName);
    }

    private Integer parsePollInterval(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private JsonObject buildUpdateResultJson(ConnectionUpdateResult result) {
        JsonObject response = new JsonObject();
        response.addProperty("name", result.name());
        response.addProperty("success", result.success());
        response.addProperty("message", result.message());
        response.addProperty("timestamp", result.timestamp());
        return response;
    }

    @Override
    public List<? extends ExtensionPoint<?>> getExtensionPoints() {
        return List.of(new MtconnectConnectionExtensionPoint());
    }

    @Override
    public boolean isMakerEditionCompatible() {
        return true;
    }
}
