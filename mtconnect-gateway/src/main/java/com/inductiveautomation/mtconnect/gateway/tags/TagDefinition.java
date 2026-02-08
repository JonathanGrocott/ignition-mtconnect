package com.inductiveautomation.mtconnect.gateway.tags;

public final class TagDefinition {
    private final String path;
    private final TagValueType valueType;
    private final String units;
    private final String description;
    private final String dataItemId;

    public TagDefinition(
            String path,
            TagValueType valueType,
            String units,
            String description,
            String dataItemId
    ) {
        this.path = path;
        this.valueType = valueType;
        this.units = units;
        this.description = description;
        this.dataItemId = dataItemId;
    }

    public String getPath() {
        return path;
    }

    public TagValueType getValueType() {
        return valueType;
    }

    public String getUnits() {
        return units;
    }

    public String getDescription() {
        return description;
    }

    public String getDataItemId() {
        return dataItemId;
    }
}
