package com.inductiveautomation.mtconnect.common.model;

import java.util.Collections;
import java.util.List;

public final class MtconnectDevice {
    private final String id;
    private final String name;
    private final String uuid;
    private final String description;
    private final List<MtconnectDataItem> dataItems;
    private final List<MtconnectComponent> components;

    public MtconnectDevice(
            String id,
            String name,
            String uuid,
            String description,
            List<MtconnectDataItem> dataItems,
            List<MtconnectComponent> components
    ) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.description = description;
        this.dataItems = dataItems == null ? List.of() : List.copyOf(dataItems);
        this.components = components == null ? List.of() : List.copyOf(components);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDescription() {
        return description;
    }

    public List<MtconnectDataItem> getDataItems() {
        return Collections.unmodifiableList(dataItems);
    }

    public List<MtconnectComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }
}
