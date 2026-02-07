package com.openclaw.ignition.mtconnect.common.parser;

import com.openclaw.ignition.mtconnect.common.model.MtconnectComponent;
import com.openclaw.ignition.mtconnect.common.model.MtconnectComponentStream;
import com.openclaw.ignition.mtconnect.common.model.MtconnectConditionLevel;
import com.openclaw.ignition.mtconnect.common.model.MtconnectDataItem;
import com.openclaw.ignition.mtconnect.common.model.MtconnectDataItemCategory;
import com.openclaw.ignition.mtconnect.common.model.MtconnectDevice;
import com.openclaw.ignition.mtconnect.common.model.MtconnectDeviceStream;
import com.openclaw.ignition.mtconnect.common.model.MtconnectDevices;
import com.openclaw.ignition.mtconnect.common.model.MtconnectHeader;
import com.openclaw.ignition.mtconnect.common.model.MtconnectObservation;
import com.openclaw.ignition.mtconnect.common.model.MtconnectStreams;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MtconnectXmlParser {
    public MtconnectDevices parseDevices(String xml) {
        Document document = parse(xml);
        Element root = document.getDocumentElement();
        Element headerElement = getFirstChildElementByLocalName(root, "Header");
        MtconnectHeader header = parseHeader(headerElement);

        Element devicesElement = getFirstChildElementByLocalName(root, "Devices");
        List<MtconnectDevice> devices = new ArrayList<>();
        if (devicesElement != null) {
            for (Element deviceElement : getChildElementsByLocalName(devicesElement, "Device")) {
                devices.add(parseDevice(deviceElement));
            }
        }

        return new MtconnectDevices(header, devices);
    }

    public MtconnectStreams parseStreams(String xml) {
        Document document = parse(xml);
        Element root = document.getDocumentElement();
        Element headerElement = getFirstChildElementByLocalName(root, "Header");
        MtconnectHeader header = parseHeader(headerElement);

        Element streamsElement = getFirstChildElementByLocalName(root, "Streams");
        List<MtconnectDeviceStream> deviceStreams = new ArrayList<>();
        if (streamsElement != null) {
            for (Element deviceStreamElement : getChildElementsByLocalName(streamsElement, "DeviceStream")) {
                deviceStreams.add(parseDeviceStream(deviceStreamElement));
            }
        }

        return new MtconnectStreams(header, deviceStreams);
    }

    private Document parse(String xml) {
        if (xml == null || xml.isBlank()) {
            throw new MtconnectParseException("MTConnect XML was empty");
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new MtconnectParseException("Failed to parse MTConnect XML", ex);
        }
    }

    private MtconnectHeader parseHeader(Element headerElement) {
        if (headerElement == null) {
            return new MtconnectHeader(null, null, null, null, null, null, null, null);
        }
        return new MtconnectHeader(
                getAttribute(headerElement, "creationTime"),
                getAttribute(headerElement, "sender"),
                getAttribute(headerElement, "version"),
                parseLong(getAttribute(headerElement, "instanceId")),
                parseLong(getAttribute(headerElement, "bufferSize")),
                parseLong(getAttribute(headerElement, "nextSequence")),
                parseLong(getAttribute(headerElement, "firstSequence")),
                parseLong(getAttribute(headerElement, "lastSequence"))
        );
    }

    private MtconnectDevice parseDevice(Element deviceElement) {
        String description = null;
        Element descriptionElement = getFirstChildElementByLocalName(deviceElement, "Description");
        if (descriptionElement != null) {
            description = textValue(descriptionElement);
        }

        List<MtconnectDataItem> dataItems = new ArrayList<>();
        Element dataItemsElement = getFirstChildElementByLocalName(deviceElement, "DataItems");
        if (dataItemsElement != null) {
            dataItems.addAll(parseDataItems(dataItemsElement));
        }

        List<MtconnectComponent> components = new ArrayList<>();
        Element componentsElement = getFirstChildElementByLocalName(deviceElement, "Components");
        if (componentsElement != null) {
            components.addAll(parseComponents(componentsElement));
        }

        return new MtconnectDevice(
                getAttribute(deviceElement, "id"),
                getAttribute(deviceElement, "name"),
                getAttribute(deviceElement, "uuid"),
                description,
                dataItems,
                components
        );
    }

    private List<MtconnectComponent> parseComponents(Element componentsElement) {
        List<MtconnectComponent> components = new ArrayList<>();
        for (Element componentElement : getChildElements(componentsElement)) {
            components.add(parseComponent(componentElement));
        }
        return components;
    }

    private MtconnectComponent parseComponent(Element componentElement) {
        List<MtconnectDataItem> dataItems = new ArrayList<>();
        Element dataItemsElement = getFirstChildElementByLocalName(componentElement, "DataItems");
        if (dataItemsElement != null) {
            dataItems.addAll(parseDataItems(dataItemsElement));
        }

        List<MtconnectComponent> components = new ArrayList<>();
        Element componentsElement = getFirstChildElementByLocalName(componentElement, "Components");
        if (componentsElement != null) {
            components.addAll(parseComponents(componentsElement));
        }

        return new MtconnectComponent(
                getAttribute(componentElement, "id"),
                getAttribute(componentElement, "name"),
                componentElement.getLocalName(),
                dataItems,
                components
        );
    }

    private List<MtconnectDataItem> parseDataItems(Element dataItemsElement) {
        List<MtconnectDataItem> dataItems = new ArrayList<>();
        for (Element dataItemElement : getChildElementsByLocalName(dataItemsElement, "DataItem")) {
            dataItems.add(new MtconnectDataItem(
                    getAttribute(dataItemElement, "id"),
                    getAttribute(dataItemElement, "name"),
                    getAttribute(dataItemElement, "type"),
                    getAttribute(dataItemElement, "subType"),
                    MtconnectDataItemCategory.fromString(getAttribute(dataItemElement, "category")),
                    getAttribute(dataItemElement, "units"),
                    getAttribute(dataItemElement, "nativeUnits")
            ));
        }
        return dataItems;
    }

    private MtconnectDeviceStream parseDeviceStream(Element deviceStreamElement) {
        List<MtconnectComponentStream> componentStreams = new ArrayList<>();
        for (Element componentStreamElement : getChildElementsByLocalName(deviceStreamElement, "ComponentStream")) {
            componentStreams.add(parseComponentStream(componentStreamElement));
        }

        return new MtconnectDeviceStream(
                getAttribute(deviceStreamElement, "name"),
                getAttribute(deviceStreamElement, "uuid"),
                componentStreams
        );
    }

    private MtconnectComponentStream parseComponentStream(Element componentStreamElement) {
        List<MtconnectObservation> observations = new ArrayList<>();
        for (Element groupElement : getChildElements(componentStreamElement)) {
            MtconnectDataItemCategory category = MtconnectDataItemCategory.fromString(groupElement.getLocalName());
            if (category == null) {
                continue;
            }
            for (Element observationElement : getChildElements(groupElement)) {
                observations.add(parseObservation(observationElement, category, groupElement.getLocalName()));
            }
        }

        return new MtconnectComponentStream(
                getAttribute(componentStreamElement, "component"),
                getAttribute(componentStreamElement, "name"),
                getAttribute(componentStreamElement, "componentId"),
                observations
        );
    }

    private MtconnectObservation parseObservation(
            Element observationElement,
            MtconnectDataItemCategory category,
            String groupName
    ) {
        String observationType = getAttribute(observationElement, "type");
        if (observationType == null) {
            observationType = observationElement.getLocalName();
        }

        MtconnectConditionLevel conditionLevel = null;
        if (MtconnectDataItemCategory.CONDITION == category) {
            conditionLevel = MtconnectConditionLevel.fromString(observationElement.getLocalName());
        }

        return new MtconnectObservation(
                getAttribute(observationElement, "dataItemId"),
                getAttribute(observationElement, "name"),
                observationType,
                category,
                getAttribute(observationElement, "subType"),
                getAttribute(observationElement, "units"),
                getAttribute(observationElement, "timestamp"),
                parseLong(getAttribute(observationElement, "sequence")),
                textValue(observationElement),
                conditionLevel
        );
    }

    private String textValue(Element element) {
        if (element == null) {
            return null;
        }
        String value = element.getTextContent();
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    private String getAttribute(Element element, String name) {
        if (element == null || !element.hasAttribute(name)) {
            return null;
        }
        String value = element.getAttribute(name);
        return value == null || value.isBlank() ? null : value;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private List<Element> getChildElementsByLocalName(Element parent, String localName) {
        List<Element> matches = new ArrayList<>();
        for (Element child : getChildElements(parent)) {
            if (localName.equals(child.getLocalName())) {
                matches.add(child);
            }
        }
        return matches;
    }

    private Element getFirstChildElementByLocalName(Element parent, String localName) {
        for (Element child : getChildElements(parent)) {
            if (localName.equals(child.getLocalName())) {
                return child;
            }
        }
        return null;
    }

    private List<Element> getChildElements(Element parent) {
        List<Element> children = new ArrayList<>();
        if (parent == null) {
            return children;
        }
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                children.add((Element) node);
            }
        }
        return children;
    }
}
