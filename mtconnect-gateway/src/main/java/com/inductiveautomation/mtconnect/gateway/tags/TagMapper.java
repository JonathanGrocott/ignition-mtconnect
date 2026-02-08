package com.inductiveautomation.mtconnect.gateway.tags;

import com.inductiveautomation.mtconnect.common.model.MtconnectComponent;
import com.inductiveautomation.mtconnect.common.model.MtconnectDataItem;
import com.inductiveautomation.mtconnect.common.model.MtconnectDataItemCategory;
import com.inductiveautomation.mtconnect.common.model.MtconnectDevice;
import com.inductiveautomation.mtconnect.common.model.MtconnectDevices;
import com.inductiveautomation.mtconnect.common.model.MtconnectObservation;
import com.inductiveautomation.mtconnect.common.model.MtconnectStreams;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagMapper {
    private final TagPathBuilder pathBuilder;
    private final String providerName;
    private final Map<String, TagDefinition> dataItemIndex = new HashMap<>();

    public TagMapper(TagPathBuilder pathBuilder, String providerName) {
        this.pathBuilder = pathBuilder == null ? new TagPathBuilder() : pathBuilder;
        this.providerName = providerName;
    }

    public List<TagDefinition> buildDefinitions(MtconnectDevices devices) {
        dataItemIndex.clear();
        List<TagDefinition> definitions = new ArrayList<>();
        if (devices == null || devices.getDevices().isEmpty()) {
            return definitions;
        }
        for (MtconnectDevice device : devices.getDevices()) {
            List<String> basePath = new ArrayList<>();
            basePath.add(device.getName() == null ? "Device" : device.getName());
            addDataItems(definitions, basePath, device.getDataItems(), device.getDescription());
            for (MtconnectComponent component : device.getComponents()) {
                addComponent(definitions, basePath, component);
            }
        }
        return definitions;
    }

    public List<TagUpdate> buildUpdates(MtconnectStreams streams) {
        List<TagUpdate> updates = new ArrayList<>();
        if (streams == null || streams.getDeviceStreams().isEmpty()) {
            return updates;
        }
        streams.getDeviceStreams().forEach(deviceStream ->
                deviceStream.getComponentStreams().forEach(componentStream ->
                        componentStream.getObservations().forEach(observation -> {
                            TagDefinition definition = dataItemIndex.get(observation.getDataItemId());
                            if (definition == null) {
                                return;
                            }
                            updates.add(new TagUpdate(
                                    definition.getPath(),
                                    coerceValue(definition.getValueType(), observation.getValue()),
                                    observation.getTimestamp(),
                                    toQuality(observation)
                            ));
                        })
                )
        );
        return updates;
    }

    private void addComponent(
            List<TagDefinition> definitions,
            List<String> parentPath,
            MtconnectComponent component
    ) {
        List<String> path = new ArrayList<>(parentPath);
        String componentName = component.getName();
        if (componentName == null || componentName.isBlank()) {
            componentName = component.getType();
        }
        path.add(componentName == null ? "Component" : componentName);
        addDataItems(definitions, path, component.getDataItems(), null);
        for (MtconnectComponent child : component.getComponents()) {
            addComponent(definitions, path, child);
        }
    }

    private void addDataItems(
            List<TagDefinition> definitions,
            List<String> parentPath,
            List<MtconnectDataItem> dataItems,
            String description
    ) {
        for (MtconnectDataItem dataItem : dataItems) {
            List<String> path = new ArrayList<>(parentPath);
            String itemName = dataItem.getName();
            if (itemName == null || itemName.isBlank()) {
                itemName = dataItem.getType();
            }
            if (dataItem.getSubType() != null && !dataItem.getSubType().isBlank()) {
                itemName = itemName + " " + dataItem.getSubType();
            }
            path.add(itemName == null ? "DataItem" : itemName);
            String tagPath = pathBuilder.buildPath(providerName, path);
            TagValueType type = mapValueType(dataItem.getCategory());
            TagDefinition definition = new TagDefinition(
                    tagPath,
                    type,
                    dataItem.getUnits(),
                    description,
                    dataItem.getId()
            );
            definitions.add(definition);
            if (dataItem.getId() != null && !dataItem.getId().isBlank()) {
                dataItemIndex.put(dataItem.getId(), definition);
            }
        }
    }

    private TagValueType mapValueType(MtconnectDataItemCategory category) {
        if (category == null) {
            return TagValueType.STRING;
        }
        return switch (category) {
            case SAMPLE -> TagValueType.FLOAT;
            case EVENT, CONDITION -> TagValueType.STRING;
        };
    }

    private Object coerceValue(TagValueType valueType, String value) {
        if (value == null) {
            return null;
        }
        if (valueType == TagValueType.FLOAT) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        if (valueType == TagValueType.BOOLEAN) {
            return Boolean.parseBoolean(value);
        }
        return value;
    }

    private TagQualityState toQuality(MtconnectObservation observation) {
        if (observation.getCategory() == MtconnectDataItemCategory.CONDITION) {
            if (observation.getConditionLevel() == null) {
                return TagQualityState.UNCERTAIN;
            }
            return switch (observation.getConditionLevel()) {
                case NORMAL -> TagQualityState.GOOD;
                case WARNING -> TagQualityState.UNCERTAIN;
                case FAULT, UNAVAILABLE -> TagQualityState.BAD;
            };
        }
        return TagQualityState.GOOD;
    }
}
