package com.inductiveautomation.mtconnect.common.model;

public enum MtconnectConditionLevel {
    NORMAL,
    WARNING,
    FAULT,
    UNAVAILABLE;

    public static MtconnectConditionLevel fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (MtconnectConditionLevel level : values()) {
            if (level.name().equalsIgnoreCase(value)) {
                return level;
            }
        }
        return null;
    }
}
