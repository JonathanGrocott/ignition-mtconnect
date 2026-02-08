package com.inductiveautomation.mtconnect.common.model;

import java.util.Collections;
import java.util.List;

public final class MtconnectDevices {
    private final MtconnectHeader header;
    private final List<MtconnectDevice> devices;

    public MtconnectDevices(MtconnectHeader header, List<MtconnectDevice> devices) {
        this.header = header;
        this.devices = devices == null ? List.of() : List.copyOf(devices);
    }

    public MtconnectHeader getHeader() {
        return header;
    }

    public List<MtconnectDevice> getDevices() {
        return Collections.unmodifiableList(devices);
    }
}
