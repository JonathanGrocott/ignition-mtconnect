package com.openclaw.ignition.mtconnect.common.model;

import java.util.Collections;
import java.util.List;

public final class MtconnectDeviceStream {
    private final String name;
    private final String uuid;
    private final List<MtconnectComponentStream> componentStreams;

    public MtconnectDeviceStream(
            String name,
            String uuid,
            List<MtconnectComponentStream> componentStreams
    ) {
        this.name = name;
        this.uuid = uuid;
        this.componentStreams = componentStreams == null ? List.of() : List.copyOf(componentStreams);
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public List<MtconnectComponentStream> getComponentStreams() {
        return Collections.unmodifiableList(componentStreams);
    }
}
