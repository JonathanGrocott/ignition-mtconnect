package com.openclaw.ignition.mtconnect.common.model;

public final class MtconnectHeader {
    private final String creationTime;
    private final String sender;
    private final String version;
    private final Long instanceId;
    private final Long bufferSize;
    private final Long nextSequence;
    private final Long firstSequence;
    private final Long lastSequence;

    public MtconnectHeader(
            String creationTime,
            String sender,
            String version,
            Long instanceId,
            Long bufferSize,
            Long nextSequence,
            Long firstSequence,
            Long lastSequence
    ) {
        this.creationTime = creationTime;
        this.sender = sender;
        this.version = version;
        this.instanceId = instanceId;
        this.bufferSize = bufferSize;
        this.nextSequence = nextSequence;
        this.firstSequence = firstSequence;
        this.lastSequence = lastSequence;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public String getSender() {
        return sender;
    }

    public String getVersion() {
        return version;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public Long getBufferSize() {
        return bufferSize;
    }

    public Long getNextSequence() {
        return nextSequence;
    }

    public Long getFirstSequence() {
        return firstSequence;
    }

    public Long getLastSequence() {
        return lastSequence;
    }
}
