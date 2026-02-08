package com.inductiveautomation.mtconnect.gateway.config;

import com.inductiveautomation.ignition.common.resourcecollection.ResourceType;
import com.inductiveautomation.ignition.gateway.config.ResourceTypeMeta;
import com.inductiveautomation.ignition.gateway.config.ValidationErrors;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.DefaultValue;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Description;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.FormCategory;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.FormField;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Label;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Minimum;
import com.inductiveautomation.ignition.gateway.dataroutes.openapi.annotations.Required;
import com.inductiveautomation.ignition.gateway.web.nav.FormFieldType;
import com.inductiveautomation.mtconnect.common.MtconnectConstants;

public record MtconnectConnectionResource(
        @FormCategory("CONNECTION")
        @Label("Agent URL")
        @FormField(FormFieldType.TEXT)
        @DefaultValue("http://localhost:5000")
        @Required
        @Description("Base URL for the MTConnect agent.")
        String agentUrl,

        @FormCategory("CONNECTION")
        @Label("Device Name Filter")
        @FormField(FormFieldType.TEXT)
        @Description("Optional device name for per-device endpoints.")
        String deviceName,

        @FormCategory("POLLING")
        @Label("Poll Interval (ms)")
        @FormField(FormFieldType.NUMBER)
        @DefaultValue("1000")
        @Minimum("200")
        @Description("Polling interval for /current in milliseconds.")
        Integer pollIntervalMs,

        @FormCategory("TAGS")
        @Label("Tag Provider Name")
        @FormField(FormFieldType.TEXT)
        @DefaultValue("MTConnect")
        @Required
        @Description("Managed tag provider name for MTConnect tags.")
        String tagProviderName
) {
    public static final ResourceType RESOURCE_TYPE =
            new ResourceType(MtconnectConstants.MODULE_ID, "mtconnect-connection");

    public static final MtconnectConnectionResource DEFAULT = new MtconnectConnectionResource(
            "http://localhost:5000",
            null,
            1000,
            "MTConnect"
    );

    public static final ResourceTypeMeta<MtconnectConnectionResource> META =
            ResourceTypeMeta.newBuilder(MtconnectConnectionResource.class)
                    .resourceType(RESOURCE_TYPE)
                    .categoryName("MTConnect Connection")
                    .defaultConfig(DEFAULT)
                    .buildValidator((resource, validator) -> {
                        resource.validate(validator);
                    })
                    .build();

    public MtconnectConnectionResource {
        if (agentUrl == null || agentUrl.isBlank()) {
            agentUrl = DEFAULT.agentUrl();
        }
        if (pollIntervalMs == null || pollIntervalMs < 200) {
            pollIntervalMs = DEFAULT.pollIntervalMs();
        }
        if (tagProviderName == null || tagProviderName.isBlank()) {
            tagProviderName = DEFAULT.tagProviderName();
        }
    }

    void validate(ValidationErrors.Builder errors) {
        errors.checkField(agentUrl != null && !agentUrl.isBlank(),
                "agentUrl",
                "Agent URL is required");
        errors.checkField(pollIntervalMs != null && pollIntervalMs >= 200,
                "pollIntervalMs",
                "Poll interval must be at least 200 ms");
        errors.checkField(tagProviderName != null && !tagProviderName.isBlank(),
                "tagProviderName",
                "Tag provider name is required");
    }
}
