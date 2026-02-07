package com.openclaw.ignition.mtconnect.common.client;

import com.openclaw.ignition.mtconnect.common.model.MtconnectDevices;
import com.openclaw.ignition.mtconnect.common.model.MtconnectStreams;

public interface MtconnectClient {
    MtconnectDevices probe(String deviceName);

    MtconnectStreams current(String deviceName, Long at);

    MtconnectStreams sample(String deviceName, Long from, Integer count);
}
