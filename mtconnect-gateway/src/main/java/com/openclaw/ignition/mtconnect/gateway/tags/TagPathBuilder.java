package com.openclaw.ignition.mtconnect.gateway.tags;

import java.util.ArrayList;
import java.util.List;

public class TagPathBuilder {
    public String buildPath(String providerName, List<String> segments) {
        List<String> cleaned = new ArrayList<>();
        for (String segment : segments) {
            String sanitized = sanitize(segment);
            if (!sanitized.isBlank()) {
                cleaned.add(sanitized);
            }
        }
        StringBuilder builder = new StringBuilder();
        if (providerName != null && !providerName.isBlank()) {
            builder.append("[").append(providerName).append("]");
        }
        for (String segment : cleaned) {
            if (builder.length() > 0 && builder.charAt(builder.length() - 1) != ']') {
                builder.append('/');
            } else if (builder.length() > 0 && builder.charAt(builder.length() - 1) == ']') {
                builder.append('/');
            }
            builder.append(segment);
        }
        return builder.toString();
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        String sanitized = value.trim();
        if (sanitized.isEmpty()) {
            return "";
        }
        sanitized = sanitized.replace('\\', '/');
        sanitized = sanitized.replaceAll("[\r\n\t]", " ");
        sanitized = sanitized.replace("[", "").replace("]", "");
        sanitized = sanitized.replaceAll("/{2,}", "/");
        return sanitized;
    }
}
