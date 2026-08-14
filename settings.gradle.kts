pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
// Declares where Gradle may download a JDK when the build's toolchain (21) is not installed
// locally. Without it, auto-provisioning still works but is deprecated and fails under Gradle 10.
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MeteoriteLandings"
include(":app")
