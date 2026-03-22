pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}

include(":skid")
include(":skid-test")

include("skid-test-annotations")
include("skid-test-processor")