plugins {
    id("io.ia.sdk.modl")
}

dependencies {
    modlImplementation(project(":mtconnect-common"))
    modlImplementation(project(":mtconnect-gateway"))
}

ignitionModule {
    name.set(property("moduleName") as String)
    fileName.set("mtconnect")
    id.set(property("moduleId") as String)
    moduleVersion.set(property("moduleVersion") as String)
    moduleDescription.set("MTConnect integration for Ignition 8.3")
    requiredIgnitionVersion.set(property("ignitionVersion") as String)
    projectScopes.set(
        mapOf(
            ":mtconnect-common" to "G",
            ":mtconnect-gateway" to "G"
        )
    )
    hooks.set(mapOf("com.openclaw.ignition.mtconnect.gateway.GatewayHook" to "G"))
    skipModlSigning.set(true)
}

tasks.named("writeModuleXml") {
    doLast {
        val moduleXmlFile = file("build/moduleContent/module.xml")
        if (!moduleXmlFile.exists()) {
            return@doLast
        }

        val version = project.version.toString()
        val moduleId = project.findProperty("moduleId").toString()
        val moduleName = project.findProperty("moduleName").toString()
        val ignitionVersion = project.findProperty("ignitionVersion").toString()

        val jarFiles = file("build/moduleContent")
            .listFiles { file -> file.isFile && file.name.endsWith(".jar") }
            ?.map { it.name }
            ?.filterNot { it.startsWith("logback-") || it.startsWith("slf4j-") }
            ?.filterNot { it.startsWith("mtconnect-build-") }
            ?.sorted()
            ?: emptyList()

        val moduleXml = buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            append("<modules>\n")
            append("\t<module>\n")
            append("\t\t<name>").append(moduleName).append("</name>\n")
            append("\t\t<id>").append(moduleId).append("</id>\n")
            append("\t\t<version>").append(version).append("</version>\n")
            append("\t\t<description>MTConnect integration for Ignition 8.3</description>\n")
            append("\t\t<requiredIgnitionVersion>").append(ignitionVersion).append("</requiredIgnitionVersion>\n")
            append("\t\t<freeModule>false</freeModule>\n")
            append("\t\t<hook scope=\"G\">com.openclaw.ignition.mtconnect.gateway.GatewayHook</hook>\n")
            append("\t\t<requiredFrameworkVersion>8</requiredFrameworkVersion>\n")

            jarFiles.forEach { jarName ->
                append("\t\t<jar scope=\"G\">").append(jarName).append("</jar>\n")
            }

            append("\t</module>\n")
            append("</modules>\n")
        }

        moduleXmlFile.writeText(moduleXml)
    }
}
