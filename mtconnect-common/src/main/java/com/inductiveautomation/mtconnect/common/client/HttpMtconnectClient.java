package com.inductiveautomation.mtconnect.common.client;

import com.inductiveautomation.mtconnect.common.model.MtconnectDevices;
import com.inductiveautomation.mtconnect.common.model.MtconnectStreams;
import com.inductiveautomation.mtconnect.common.parser.MtconnectXmlParser;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpMtconnectClient implements MtconnectClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final MtconnectXmlParser parser;
    private final Duration timeout;

    public HttpMtconnectClient(String baseUrl) {
        this(baseUrl, HttpClient.newHttpClient(), new MtconnectXmlParser(), Duration.ofSeconds(10));
    }

    public HttpMtconnectClient(String baseUrl, HttpClient httpClient, MtconnectXmlParser parser, Duration timeout) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("baseUrl is required");
        }
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.httpClient = httpClient == null ? HttpClient.newHttpClient() : httpClient;
        this.parser = parser == null ? new MtconnectXmlParser() : parser;
        this.timeout = timeout == null ? Duration.ofSeconds(10) : timeout;
    }

    @Override
    public MtconnectDevices probe(String deviceName) {
        String path = deviceName == null || deviceName.isBlank()
                ? "probe"
                : encodeSegment(deviceName) + "/probe";
        return parser.parseDevices(doGet(buildUrl(path, Map.of())));
    }

    @Override
    public MtconnectStreams current(String deviceName, Long at) {
        String path = deviceName == null || deviceName.isBlank()
                ? "current"
                : encodeSegment(deviceName) + "/current";
        Map<String, String> params = new LinkedHashMap<>();
        if (at != null) {
            params.put("at", String.valueOf(at));
        }
        return parser.parseStreams(doGet(buildUrl(path, params)));
    }

    @Override
    public MtconnectStreams sample(String deviceName, Long from, Integer count) {
        String path = deviceName == null || deviceName.isBlank()
                ? "sample"
                : encodeSegment(deviceName) + "/sample";
        Map<String, String> params = new LinkedHashMap<>();
        if (from != null) {
            params.put("from", String.valueOf(from));
        }
        if (count != null) {
            params.put("count", String.valueOf(count));
        }
        return parser.parseStreams(doGet(buildUrl(path, params)));
    }

    private String doGet(URI uri) {
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(timeout)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                throw new MtconnectClientException("MTConnect request failed with status " + status);
            }
            return response.body();
        } catch (IOException ex) {
            throw new MtconnectClientException("MTConnect request failed", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new MtconnectClientException("MTConnect request failed", ex);
        }
    }

    private URI buildUrl(String path, Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        builder.append(baseUrl);
        if (!baseUrl.endsWith("/")) {
            builder.append("/");
        }
        builder.append(path.startsWith("/") ? path.substring(1) : path);
        if (!params.isEmpty()) {
            builder.append("?");
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!first) {
                    builder.append("&");
                }
                first = false;
                builder.append(encode(entry.getKey()));
                builder.append("=");
                builder.append(encode(entry.getValue()));
            }
        }
        return URI.create(builder.toString());
    }

    private String encodeSegment(String value) {
        return encode(value).replace("+", "%20");
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String trimTrailingSlash(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == '/') {
            end--;
        }
        return value.substring(0, end);
    }
}
