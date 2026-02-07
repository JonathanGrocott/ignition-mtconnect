package com.openclaw.ignition.mtconnect.common.model;

import java.util.Collections;
import java.util.List;

public final class MtconnectStreams {
    private final MtconnectHeader header;
    private final List<MtconnectDeviceStream> deviceStreams;

    public MtconnectStreams(MtconnectHeader header, List<MtconnectDeviceStream> deviceStreams) {
        this.header = header;
        this.deviceStreams = deviceStreams == null ? List.of() : List.copyOf(deviceStreams);
    }

    public MtconnectHeader getHeader() {
        return header;
    }

    public List<MtconnectDeviceStream> getDeviceStreams() {
        return Collections.unmodifiableList(deviceStreams);
    }
}
