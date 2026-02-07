pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://nexus.inductiveautomation.com/repository/inductiveautomation-releases/")
        }
    }
}

rootProject.name = "ignition-mtconnect"

include(
    "mtconnect-common",
    "mtconnect-gateway",
    "mtconnect-build"
)
