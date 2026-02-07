package com.openclaw.ignition.mtconnect.common.model;

public final class MtconnectObservation {
    private final String dataItemId;
    private final String name;
    private final String observationType;
    private final MtconnectDataItemCategory category;
    private final String subType;
    private final String units;
    private final String timestamp;
    private final Long sequence;
    private final String value;
    private final MtconnectConditionLevel conditionLevel;

    public MtconnectObservation(
            String dataItemId,
            String name,
            String observationType,
            MtconnectDataItemCategory category,
            String subType,
            String units,
            String timestamp,
            Long sequence,
            String value,
            MtconnectConditionLevel conditionLevel
    ) {
        this.dataItemId = dataItemId;
        this.name = name;
        this.observationType = observationType;
        this.category = category;
        this.subType = subType;
        this.units = units;
        this.timestamp = timestamp;
        this.sequence = sequence;
        this.value = value;
        this.conditionLevel = conditionLevel;
    }

    public String getDataItemId() {
        return dataItemId;
    }

    public String getName() {
        return name;
    }

    public String getObservationType() {
        return observationType;
    }

    public MtconnectDataItemCategory getCategory() {
        return category;
    }

    public String getSubType() {
        return subType;
    }

    public String getUnits() {
        return units;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Long getSequence() {
        return sequence;
    }

    public String getValue() {
        return value;
    }

    public MtconnectConditionLevel getConditionLevel() {
        return conditionLevel;
    }
}
