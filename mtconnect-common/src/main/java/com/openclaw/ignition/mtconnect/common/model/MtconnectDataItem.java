package com.openclaw.ignition.mtconnect.common.model;

public final class MtconnectDataItem {
    private final String id;
    private final String name;
    private final String type;
    private final String subType;
    private final MtconnectDataItemCategory category;
    private final String units;
    private final String nativeUnits;

    public MtconnectDataItem(
            String id,
            String name,
            String type,
            String subType,
            MtconnectDataItemCategory category,
            String units,
            String nativeUnits
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.subType = subType;
        this.category = category;
        this.units = units;
        this.nativeUnits = nativeUnits;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public MtconnectDataItemCategory getCategory() {
        return category;
    }

    public String getUnits() {
        return units;
    }

    public String getNativeUnits() {
        return nativeUnits;
    }
}
