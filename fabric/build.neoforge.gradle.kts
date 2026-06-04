plugins {
	id("net.neoforged.gradle.userdev") version "7.0.163"
	id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

version = "${parent?.name}-${property("mod.version")}+${stonecutter.current.project}"
group = project.property("maven_group").toString()

base {
	archivesName.set(property("archives_base_name").toString())
}

repositories {
	mavenCentral()
	mavenLocal()
}

val configuredVersion = "0.3"

java {
	val j21 = stonecutter.eval(stonecutter.current.version, ">=1.20.5")
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(if (j21) 21 else 17))
	}
	sourceCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	targetCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
}

minecraft {
	version.set(stonecutter.current.project)
}

dependencies {
	implementation("net.neoforged:neoforge:${stonecutter.current.project}-${property("deps.neoforge_version")}")
	
	implementation(include("de.clickism:configured-core:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-yaml:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-json:${configuredVersion}")!!)
	modImplementation(include("de.clickism:configured-neoforge-command-adapter:${configuredVersion}")!!)
	
	implementation(include("org.yaml:snakeyaml:2.0")!!)
}

tasks.processResources {
	val props = mapOf(
		"version" to version,
		"targetVersion" to project.property("mod.mc_version"),
		"minecraftVersion" to stonecutter.current.version,
		"neoforgeVersion" to property("deps.neoforge_version")
	)
	filesMatching("META-INF/neoforge.mods.toml") {
		expand(props)
	}
	inputs.properties(props)
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName.get()}" }
	}
}

publishMods {
	displayName.set("ClickMobs ${property("mod.version")} for NeoForge")
	file.set(tasks.jar.get().archiveFile)
	version.set(project.version.toString())
	changelog.set(rootProject.file("fabric/CHANGELOG.md").readText())
	type.set(STABLE)
	modLoaders.add("neoforge")
	val mcVersions = property("mod.target_mc_versions").toString().split(',')
	modrinth {
		accessToken.set(System.getenv("MODRINTH_TOKEN"))
		projectId.set("tRdRT5jS")
		requires("neoforge")
		minecraftVersions.addAll(mcVersions)
	}
	curseforge {
		accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
		projectId.set("1179556")
		clientRequired.set(false)
		serverRequired.set(true)
		requires("neoforge")
		minecraftVersions.addAll(mcVersions)
	}
}
