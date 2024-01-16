plugins {
    kotlin("jvm")
    id("fabric-loom")
    `maven-publish`
    java
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = property("maven_group")!!
version = "${property("mod_version")}+${property("minecraft_version_group")}"

repositories {
    mavenCentral()
    maven("https://maven.shedaniel.me/")
    maven("https://maven.nucleoid.xyz/")
    maven("https://jitpack.io")
    maven("https://repo.spongepowered.org/maven/")
}

val fapiModules: Array<String> = arrayOf(
    "fabric-api-base",
    "fabric-command-api-v2",
)

dependencies {
    fun modIncludeImplement(any: Any) {
        include(modImplementation(any)!!)
    }

    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    fapiModules.forEach {
        modIncludeImplement(fabricApi.module(it, property("fabric_api_version").toString()))
    }

    modIncludeImplement("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
    modIncludeImplement("eu.pb4:placeholder-api:2.1.0+1.19.4")

    // Shadow and include some extra dependencies
    modIncludeImplement("org.spongepowered:configurate-extra-kotlin:4.1.2")
    implementation(shadow("org.spongepowered:configurate-core:4.1.2")!!)
    implementation(shadow("org.spongepowered:configurate-hocon:4.1.2")!!)
    implementation(shadow("dev.kord:kord-core:0.12.0")!!)
    implementation(shadow("com.vdurmont:emoji-java:5.1.1")!!)
}

tasks {

    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand(mutableMapOf("version" to project.version))
        }
    }

    shadowJar {
        archiveClassifier = "bundle"
        relocationPrefix = "dependencies"
        configurations = listOf(project.configurations.shadow.get())
        isEnableRelocation = true

        exclude(
            "**/META-INF/**",
            "DebugProbesKt.bin",
            "org/intellij/**",
            "org/jetbrains/annotations/**",
            "*.kotlin_module"
        )
        minimize()
    }

    remapJar {
        inputFile = file(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
    }

    jar {
        from("LICENSE")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }

        // select the repositories you want to publish to
        repositories {
            // uncomment to publish to the local maven
            // mavenLocal()
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

// configure the maven publication
