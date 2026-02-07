package com.openclaw.ignition.mtconnect.gateway.polling;

import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.openclaw.ignition.mtconnect.common.client.HttpMtconnectClient;
import com.openclaw.ignition.mtconnect.common.client.MtconnectClient;
import com.openclaw.ignition.mtconnect.common.model.MtconnectDevices;
import com.openclaw.ignition.mtconnect.common.model.MtconnectStreams;
import com.openclaw.ignition.mtconnect.gateway.config.MtconnectConnectionResource;
import com.openclaw.ignition.mtconnect.gateway.tags.IgnitionManagedTagWriter;
import com.openclaw.ignition.mtconnect.gateway.tags.ManagedTagWriter;
import com.openclaw.ignition.mtconnect.gateway.tags.MtconnectTagProvider;
import com.openclaw.ignition.mtconnect.gateway.tags.TagMapper;
import com.openclaw.ignition.mtconnect.gateway.tags.TagDefinition;
import com.openclaw.ignition.mtconnect.gateway.tags.TagQualityState;
import com.openclaw.ignition.mtconnect.gateway.tags.TagUpdate;
import com.openclaw.ignition.mtconnect.gateway.tags.TagPathBuilder;
import com.openclaw.ignition.mtconnect.gateway.tags.TagValueType;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MtconnectPollingService {
    private static final String TASK_OWNER = MtconnectPollingService.class.getName();
    private static final String TASK_PREFIX = "mtconnect-poll-";
    private static final int MAX_BACKOFF_MS = 60000;

    private final Logger logger = LoggerFactory.getLogger(MtconnectPollingService.class);
    private final GatewayContext context;
    private final String taskName;
    private final MtconnectConnectionResource config;
    private final MtconnectClient client;
    private final ManagedTagWriter tagWriter;
    private final TagMapper tagMapper;
    private final MtconnectTagProvider tagProvider;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final List<TagDefinition> dataDefinitions = new ArrayList<>();
    private final String lastPollPath;
    private final String lastErrorPath;
    private final String connectedPath;
    private final String uptimePath;
    private final String failureCountPath;
    private final String backoffRemainingPath;
    private final String backoffUntilPath;
    private final String lastSuccessPath;
    private volatile String lastPollTime;
    private volatile String lastError;
    private volatile String lastSuccessTime;
    private volatile long connectedSinceEpochMs;
    private volatile long backoffUntilEpochMs;
    private volatile int failureCount;
    private volatile int lastObservedTagCount;

    public MtconnectPollingService(GatewayContext context, String connectionName, MtconnectConnectionResource config) {
        this.context = context;
        this.config = config;
        this.taskName = TASK_PREFIX + sanitize(connectionName);
        this.client = new HttpMtconnectClient(config.agentUrl());
        TagPathBuilder pathBuilder = new TagPathBuilder();
        this.tagMapper = new TagMapper(pathBuilder, config.tagProviderName());
        this.tagWriter = new IgnitionManagedTagWriter(context, config.tagProviderName());
        this.tagProvider = new MtconnectTagProvider(tagWriter, tagMapper);
        String statusRoot = sanitize(connectionName);
        this.lastPollPath = pathBuilder.buildPath(
            config.tagProviderName(), List.of("_status", statusRoot, "LastPollTime"));
        this.lastErrorPath = pathBuilder.buildPath(
            config.tagProviderName(), List.of("_status", statusRoot, "LastError"));
        this.connectedPath = pathBuilder.buildPath(
            config.tagProviderName(), List.of("_status", statusRoot, "Connected"));
        this.uptimePath = pathBuilder.buildPath(
            config.tagProviderName(), List.of("_status", statusRoot, "UptimeSeconds"));
        this.failureCountPath = pathBuilder.buildPath(
            config.tagProviderName(), List.of("_status", statusRoot, "FailureCount"));
        this.backoffRemainingPath = pathBuilder.buildPath(
            config.tagProviderName(), List.of("_status", statusRoot, "BackoffRemainingSeconds"));
        this.backoffUntilPath = pathBuilder.buildPath(
            config.tagProviderName(), List.of("_status", statusRoot, "BackoffUntilEpochMs"));
        this.lastSuccessPath = pathBuilder.buildPath(
            config.tagProviderName(), List.of("_status", statusRoot, "LastSuccessTime"));
    }

    public void start() {
        running.set(true);
        configureStatusTags();
        try {
            initializeTags();
        } catch (Exception ex) {
            logger.warn("Failed to initialize MTConnect tags for {}", taskName, ex);
            updateError(ex);
        }
        context.getExecutionManager().register(
                TASK_OWNER,
                taskName,
                this::pollOnce,
                config.pollIntervalMs()
        );
    }

    public void stop() {
        running.set(false);
        context.getExecutionManager().unRegister(TASK_OWNER, taskName);
    }

    public String getLastPollTime() {
        return lastPollTime;
    }

    public String getLastError() {
        return lastError;
    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean isConnected() {
        return connected.get();
    }

    public long getUptimeSeconds() {
        if (!connected.get() || connectedSinceEpochMs == 0) {
            return 0;
        }
        long now = System.currentTimeMillis();
        return Math.max(0, (now - connectedSinceEpochMs) / 1000);
    }

    public int getFailureCount() {
        return failureCount;
    }

    public long getBackoffRemainingSeconds() {
        long now = System.currentTimeMillis();
        if (backoffUntilEpochMs <= now) {
            return 0;
        }
        return Math.max(0, (backoffUntilEpochMs - now) / 1000);
    }

    public String getLastSuccessTime() {
        return lastSuccessTime;
    }

    public long getBackoffUntilEpochMs() {
        return backoffUntilEpochMs;
    }

    public int getLastObservedTagCount() {
        return lastObservedTagCount;
    }

    public int getTotalTagCount() {
        return dataDefinitions.size();
    }

    private void initializeTags() {
        MtconnectDevices devices = client.probe(config.deviceName());
        dataDefinitions.clear();
        dataDefinitions.addAll(tagMapper.buildDefinitions(devices));
        tagWriter.configureTags(dataDefinitions);
        initialized.set(true);
    }

    private void pollOnce() {
        try {
            if (isInBackoff()) {
                updateBackoffCountdown();
                return;
            }
            if (!initialized.get()) {
                initializeTags();
            }
            MtconnectStreams streams = client.current(config.deviceName(), null);
            List<TagUpdate> updates = tagMapper.buildUpdates(streams);
            tagWriter.updateTags(updates);
            lastObservedTagCount = countUniquePaths(updates);
            updateStatusSuccess();
            resetBackoff();
        } catch (Exception ex) {
            updateError(ex);
            logger.debug("MTConnect poll failed for {}", taskName, ex);
        }
    }

    private int countUniquePaths(List<TagUpdate> updates) {
        if (updates == null || updates.isEmpty()) {
            return 0;
        }
        Set<String> unique = new HashSet<>();
        for (TagUpdate update : updates) {
            if (update.getPath() != null) {
                unique.add(update.getPath());
            }
        }
        return unique.size();
    }

    private void configureStatusTags() {
        List<TagDefinition> statusDefinitions = List.of(
                new TagDefinition(lastPollPath, TagValueType.STRING, null, "Last successful poll time", null),
                new TagDefinition(lastErrorPath, TagValueType.STRING, null, "Last poll error", null),
                new TagDefinition(connectedPath, TagValueType.BOOLEAN, null, "Connection state", null),
                new TagDefinition(uptimePath, TagValueType.FLOAT, null, "Uptime in seconds", null),
                new TagDefinition(failureCountPath, TagValueType.FLOAT, null, "Failure count", null),
                new TagDefinition(backoffRemainingPath, TagValueType.FLOAT, null, "Backoff remaining seconds", null),
                new TagDefinition(backoffUntilPath, TagValueType.FLOAT, null, "Backoff until epoch ms", null),
                new TagDefinition(lastSuccessPath, TagValueType.STRING, null, "Last success time", null)
        );
        tagWriter.configureTags(statusDefinitions);
    }

    private void updateStatusSuccess() {
        lastPollTime = Instant.now().toString();
        lastError = null;
        lastSuccessTime = lastPollTime;
        if (!connected.get()) {
            connected.set(true);
            connectedSinceEpochMs = System.currentTimeMillis();
        }
        List<TagUpdate> updates = List.of(
                new TagUpdate(lastPollPath, lastPollTime, null, TagQualityState.GOOD),
                new TagUpdate(lastErrorPath, null, null, TagQualityState.GOOD)
        );
        List<TagUpdate> merged = new ArrayList<>(updates);
        merged.add(new TagUpdate(connectedPath, true, null, TagQualityState.GOOD));
        merged.add(new TagUpdate(uptimePath, (double) getUptimeSeconds(), null, TagQualityState.GOOD));
        merged.add(new TagUpdate(failureCountPath, (double) failureCount, null, TagQualityState.GOOD));
        merged.add(new TagUpdate(backoffRemainingPath, 0.0, null, TagQualityState.GOOD));
        merged.add(new TagUpdate(backoffUntilPath, 0.0, null, TagQualityState.GOOD));
        merged.add(new TagUpdate(lastSuccessPath, lastSuccessTime, null, TagQualityState.GOOD));
        tagWriter.updateTags(merged);
    }

    private void updateError(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            message = ex.getClass().getSimpleName();
        }
        lastError = message;
        connected.set(false);
        connectedSinceEpochMs = 0;
        recordBackoff();
        List<TagUpdate> updates = new ArrayList<>();
        updates.add(new TagUpdate(lastErrorPath, message, null, TagQualityState.BAD));
        updates.add(new TagUpdate(lastPollPath, null, null, TagQualityState.BAD));
        updates.add(new TagUpdate(connectedPath, false, null, TagQualityState.BAD));
        updates.add(new TagUpdate(uptimePath, 0.0, null, TagQualityState.BAD));
        updates.add(new TagUpdate(failureCountPath, (double) failureCount, null, TagQualityState.BAD));
        updates.add(new TagUpdate(backoffRemainingPath, (double) getBackoffRemainingSeconds(), null, TagQualityState.BAD));
        updates.add(new TagUpdate(backoffUntilPath, (double) backoffUntilEpochMs, null, TagQualityState.BAD));
        updates.add(new TagUpdate(lastSuccessPath, lastSuccessTime, null, TagQualityState.BAD));
        for (TagDefinition definition : dataDefinitions) {
            updates.add(new TagUpdate(definition.getPath(), null, null, TagQualityState.BAD));
        }
        tagWriter.updateTags(updates);
    }

    private boolean isInBackoff() {
        long now = System.currentTimeMillis();
        return backoffUntilEpochMs > now;
    }

    private void recordBackoff() {
        failureCount = Math.min(failureCount + 1, 8);
        long base = Math.max(config.pollIntervalMs(), 200);
        long delay = base * (1L << Math.min(failureCount, 6));
        delay = Math.min(delay, MAX_BACKOFF_MS);
        backoffUntilEpochMs = System.currentTimeMillis() + delay;
    }

    private void resetBackoff() {
        failureCount = 0;
        backoffUntilEpochMs = 0;
    }

    private void updateBackoffCountdown() {
        List<TagUpdate> updates = new ArrayList<>();
        updates.add(new TagUpdate(backoffRemainingPath, (double) getBackoffRemainingSeconds(), null,
                TagQualityState.BAD));
        updates.add(new TagUpdate(backoffUntilPath, (double) backoffUntilEpochMs, null, TagQualityState.BAD));
        tagWriter.updateTags(updates);
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "default";
        }
        return value.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
