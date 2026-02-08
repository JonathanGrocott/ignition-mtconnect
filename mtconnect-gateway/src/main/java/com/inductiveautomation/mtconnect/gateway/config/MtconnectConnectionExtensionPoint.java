package com.inductiveautomation.mtconnect.gateway.config;

import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.AbstractExtensionPoint;
import com.inductiveautomation.ignition.gateway.config.ExtensionPointConfig;
import com.inductiveautomation.ignition.gateway.config.ValidationErrors;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.SchemaUtil;
import com.inductiveautomation.ignition.gateway.web.nav.ExtensionPointResourceForm;
import com.inductiveautomation.ignition.gateway.web.nav.WebUiComponent;
import java.util.Optional;

public class MtconnectConnectionExtensionPoint
        extends AbstractExtensionPoint<MtconnectConnectionResource> {
    public static final String TYPE_ID = "MTCONNECT_CONNECTION";

    public MtconnectConnectionExtensionPoint() {
        super(TYPE_ID, "MTConnect Connection", "MTConnect connection configuration");
    }

    @Override
    public ResourceType resourceType() {
        return MtconnectConnectionResource.RESOURCE_TYPE;
    }

    @Override
    public Optional<WebUiComponent> getWebUiComponent(ComponentType type) {
        return Optional.of(
                new ExtensionPointResourceForm(
                    MtconnectConnectionResource.RESOURCE_TYPE,
                        "MTConnect Connection",
                        TYPE_ID,
                        SchemaUtil.fromType(ExtensionPointConfig.class),
                        SchemaUtil.fromType(MtconnectConnectionResource.class)
                )
        );
    }

    @Override
    protected void validate(MtconnectConnectionResource settings, ValidationErrors.Builder errors) {
        settings.validate(errors);
    }
}
