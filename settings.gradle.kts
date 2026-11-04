pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}

rootProject.name = "Abacus Main"
include(":app")
// include(":asset_install_time")   // unlinked (see app/build.gradle.kts): the folder is kept, uncomment this and assetPacks there to use it again
