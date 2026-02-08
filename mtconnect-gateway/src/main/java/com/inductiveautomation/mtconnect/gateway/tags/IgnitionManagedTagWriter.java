package com.inductiveautomation.mtconnect.gateway.tags;

import com.inductiveautomation.ignition.common.model.values.QualityCode;
import com.inductiveautomation.ignition.common.sqltags.model.TagProviderMeta;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataType;
import com.inductiveautomation.ignition.common.tags.config.TagProviderValuePersistence;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.tags.managed.ManagedTagProvider;
import com.inductiveautomation.ignition.gateway.tags.managed.ManagedTagProviderConfiguration;
import java.util.List;

public class IgnitionManagedTagWriter implements ManagedTagWriter {
    private final ManagedTagProvider provider;

    public IgnitionManagedTagWriter(GatewayContext context, String providerName) {
        if (context == null) {
            throw new IllegalArgumentException("GatewayContext is required");
        }
        String resolvedProvider = providerName == null || providerName.isBlank()
                ? "MTConnect"
                : providerName;
        ManagedTagProviderConfiguration configuration = ManagedTagProviderConfiguration.builder(resolvedProvider)
                .persistTags(false)
                .allowTagCustomization(true)
                .valuePersistence(TagProviderValuePersistence.None)
                .setAttribute(TagProviderMeta.FLAG_HAS_OPCBROWSE, false)
                .build();
        this.provider = context.getTagManager().getOrCreateManagedProvider(configuration);
    }

    @Override
    public void configureTags(List<TagDefinition> definitions) {
        if (definitions == null || definitions.isEmpty()) {
            return;
        }
        for (TagDefinition definition : definitions) {
            provider.configureTag(definition.getPath(), mapDataType(definition.getValueType()));
        }
    }

    @Override
    public void updateTags(List<TagUpdate> updates) {
        if (updates == null || updates.isEmpty()) {
            return;
        }
        for (TagUpdate update : updates) {
            provider.updateValue(update.getPath(), update.getValue(), mapQuality(update.getQualityState()));
        }
    }

    public void shutdown(boolean removeTags) {
        provider.shutdown(removeTags);
    }

    private DataType mapDataType(TagValueType valueType) {
        if (valueType == null) {
            return DataType.String;
        }
        return switch (valueType) {
            case FLOAT -> DataType.Float8;
            case BOOLEAN -> DataType.Boolean;
            case STRING -> DataType.String;
        };
    }

    private QualityCode mapQuality(TagQualityState qualityState) {
        if (qualityState == null) {
            return QualityCode.Bad;
        }
        return switch (qualityState) {
            case GOOD -> QualityCode.Good;
            case UNCERTAIN -> QualityCode.Uncertain;
            case BAD -> QualityCode.Bad;
        };
    }
}
