import org.gradle.api.plugins.JavaPluginExtension

plugins {
    id("io.ia.sdk.modl") version "0.4.0" apply false
}

group = "com.inductiveautomation.mtconnect"
version = property("moduleVersion") as String

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://nexus.inductiveautomation.com/repository/inductiveautomation-releases/")
        }
        maven {
            url = uri("https://nexus.inductiveautomation.com/repository/inductiveautomation-thirdparty/")
        }
    }
}

subprojects {
    plugins.apply("java-library")

    group = rootProject.group
    version = rootProject.version

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
