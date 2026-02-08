package com.inductiveautomation.mtconnect.common.model;

import java.util.Collections;
import java.util.List;

public final class MtconnectComponent {
    private final String id;
    private final String name;
    private final String type;
    private final List<MtconnectDataItem> dataItems;
    private final List<MtconnectComponent> components;

    public MtconnectComponent(
            String id,
            String name,
            String type,
            List<MtconnectDataItem> dataItems,
            List<MtconnectComponent> components
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.dataItems = dataItems == null ? List.of() : List.copyOf(dataItems);
        this.components = components == null ? List.of() : List.copyOf(components);
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

    public List<MtconnectDataItem> getDataItems() {
        return Collections.unmodifiableList(dataItems);
    }

    public List<MtconnectComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }
}
