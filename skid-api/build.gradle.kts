import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import java.net.URI

plugins {
    kotlin("jvm")
    id("fabric-loom")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
    `maven-publish`
    `java-library`
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String
val enableSourcesJar = providers.gradleProperty("release").isPresent

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21

java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    if (enableSourcesJar) {
        // Only generate/remap sources for release publishing builds.
        withSourcesJar()
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("skid-api") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

val clientSourceSet = sourceSets.named("client")
val clientTestSourceSet = sourceSets.create("clientTest") {
    compileClasspath += clientSourceSet.get().output + clientSourceSet.get().compileClasspath
    runtimeClasspath += output + compileClasspath + clientSourceSet.get().runtimeClasspath
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    add(clientTestSourceSet.implementationConfigurationName, kotlin("test-junit5"))
    add(
        clientTestSourceSet.runtimeOnlyConfigurationName,
        "org.junit.platform:junit-platform-launcher",
    )
}

val clientTest by tasks.registering(Test::class) {
    description = "Runs tests for the client API source set."
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    testClassesDirs = clientTestSourceSet.output.classesDirs
    classpath = clientTestSourceSet.runtimeClasspath
    useJUnitPlatform()
}

tasks.check {
    dependsOn(clientTest)
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    inputs.property("kotlin_loader_version", project.property("kotlin_loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to project.version,
                "minecraft_version" to project.property("minecraft_version"),
                "loader_version" to project.property("loader_version"),
                "kotlin_loader_version" to project.property("kotlin_loader_version"),
            )
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

dokka {
    dokkaPublications.html {
        moduleName.set("SkidMC API")
        moduleVersion.set(project.version.toString())
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
        suppressObviousFunctions.set(true)
    }

    dokkaSourceSets.configureEach {
        documentedVisibilities.set(setOf(VisibilityModifier.Public))
        jdkVersion.set(targetJavaVersion)
        reportUndocumented.set(false)
        skipDeprecated.set(false)
    }

    dokkaSourceSets.named("main") {
        suppress.set(true)
    }

    dokkaSourceSets.named("client") {
        suppress.set(false)

        sourceLink {
            localDirectory.set(file("src/client/kotlin"))
            remoteUrl.set(URI("https://github.com/oni0nfr1/SkidMC/tree/main/skid-api/src/client/kotlin"))
            remoteLineSuffix.set("#L")
        }
    }
}

tasks.jar {
    from(rootProject.file("LICENSE.txt")) {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

mavenPublishing {
    @Suppress("UnstableApiUsage")
    configureBasedOnAppliedPlugins(
        javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
        sourcesJar = SourcesJar.Sources(),
    )

    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = project.group.toString(),
        artifactId = "skid-api",
        version = project.version.toString(),
    )

    pom {
        name.set("SkidMC API")
        description.set("Public API contracts for KartRider: Minecraft client mod development")
        url.set("https://github.com/oni0nfr1/SkidMC")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("oni0nfr1")
                name.set("Kim Tae Eon")
                email.set("taeun06@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/oni0nfr1/SkidMC")
            connection.set("scm:git:git://github.com/oni0nfr1/SkidMC.git")
            developerConnection.set("scm:git:ssh://git@github.com:oni0nfr1/SkidMC.git")
        }
    }
}
