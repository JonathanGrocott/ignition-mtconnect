package com.inductiveautomation.mtconnect.common.model;

import java.util.Collections;
import java.util.List;

public final class MtconnectComponentStream {
    private final String component;
    private final String name;
    private final String componentId;
    private final List<MtconnectObservation> observations;

    public MtconnectComponentStream(
            String component,
            String name,
            String componentId,
            List<MtconnectObservation> observations
    ) {
        this.component = component;
        this.name = name;
        this.componentId = componentId;
        this.observations = observations == null ? List.of() : List.copyOf(observations);
    }

    public String getComponent() {
        return component;
    }

    public String getName() {
        return name;
    }

    public String getComponentId() {
        return componentId;
    }

    public List<MtconnectObservation> getObservations() {
        return Collections.unmodifiableList(observations);
    }
}
