package com.inductiveautomation.mtconnect.gateway.tags;

import java.util.List;

public interface ManagedTagWriter {
    void configureTags(List<TagDefinition> definitions);

    void updateTags(List<TagUpdate> updates);
}
