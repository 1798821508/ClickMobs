pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.10"
}

rootProject.name = "ClickMobs"

include("fabric")

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"
    create("fabric") {
        fun version(version: String, vararg loaders: String) {
            loaders.forEach {
                this.version("$version-$it", version)
                    .buildscript = "build.$it.gradle.kts"
            }
        }
        version("1.21.1", "neoforge")
        vcsVersion = "1.21.1-neoforge"
    }
}
