plugins {
    id("net.neoforged.gradle.userdev") version "7.0.163"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

version = "ClickMobs-1.3+1.21.1-neoforge"
group = "de.clickism.clickmobs"

base {
    archivesName.set("ClickMobs")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val configuredVersion = "0.3"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

minecraft {
    version.set("1.21.1")
}

dependencies {
    implementation("net.neoforged:neoforge:1.21.1-20.4.160")
    
    implementation(include("de.clickism:configured-core:${configuredVersion}")!!)
    implementation(include("de.clickism:configured-yaml:${configuredVersion}")!!)
    implementation(include("de.clickism:configured-json:${configuredVersion}")!!)
    modImplementation(include("de.clickism:configured-neoforge-command-adapter:${configuredVersion}")!!)
    
    implementation(include("org.yaml:snakeyaml:2.0")!!)
}

tasks.processResources {
    val props = mapOf(
        "version" to version,
        "minecraftVersion" to "1.21.1"
    )
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(props)
    }
    inputs.properties(props)
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_ClickMobs" }
    }
}

publishMods {
    displayName.set("ClickMobs 1.3 for NeoForge")
    file.set(tasks.jar.get().archiveFile)
    version.set(version.toString())
    changelog.set(rootProject.file("fabric/CHANGELOG.md").readText())
    type.set(STABLE)
    modLoaders.add("neoforge")
    modrinth {
        accessToken.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("tRdRT5jS")
        requires("neoforge")
        minecraftVersions.add("1.21.1")
    }
    curseforge {
        accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
        projectId.set("1179556")
        clientRequired.set(false)
        serverRequired.set(true)
        requires("neoforge")
        minecraftVersions.add("1.21.1")
    }
}
