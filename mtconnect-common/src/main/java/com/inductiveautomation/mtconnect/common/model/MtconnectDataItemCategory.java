package com.inductiveautomation.mtconnect.common.model;

public enum MtconnectDataItemCategory {
    SAMPLE,
    EVENT,
    CONDITION;

    public static MtconnectDataItemCategory fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.endsWith("s") || normalized.endsWith("S")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if ("conditions".equalsIgnoreCase(value)) {
            normalized = "condition";
        }
        for (MtconnectDataItemCategory category : values()) {
            if (category.name().equalsIgnoreCase(normalized)) {
                return category;
            }
        }
        return null;
    }
}
