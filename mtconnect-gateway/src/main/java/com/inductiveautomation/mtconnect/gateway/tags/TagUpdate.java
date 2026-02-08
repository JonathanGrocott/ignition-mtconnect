package com.inductiveautomation.mtconnect.gateway.tags;

public final class TagUpdate {
    private final String path;
    private final Object value;
    private final String timestamp;
    private final TagQualityState qualityState;

    public TagUpdate(String path, Object value, String timestamp, TagQualityState qualityState) {
        this.path = path;
        this.value = value;
        this.timestamp = timestamp;
        this.qualityState = qualityState;
    }

    public String getPath() {
        return path;
    }

    public Object getValue() {
        return value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public TagQualityState getQualityState() {
        return qualityState;
    }
}
