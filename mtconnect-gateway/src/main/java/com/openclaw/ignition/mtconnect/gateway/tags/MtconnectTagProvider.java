package com.openclaw.ignition.mtconnect.gateway.tags;

import com.openclaw.ignition.mtconnect.common.model.MtconnectDevices;
import com.openclaw.ignition.mtconnect.common.model.MtconnectStreams;
import java.util.List;

public class MtconnectTagProvider {
    private final ManagedTagWriter tagWriter;
    private final TagMapper tagMapper;

    public MtconnectTagProvider(ManagedTagWriter tagWriter, TagMapper tagMapper) {
        this.tagWriter = tagWriter;
        this.tagMapper = tagMapper;
    }

    public void initializeTags(MtconnectDevices devices) {
        List<TagDefinition> definitions = tagMapper.buildDefinitions(devices);
        tagWriter.configureTags(definitions);
    }

    public void updateTags(MtconnectStreams streams) {
        List<TagUpdate> updates = tagMapper.buildUpdates(streams);
        tagWriter.updateTags(updates);
    }
}
