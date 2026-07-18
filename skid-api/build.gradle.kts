import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("fabric-loom")
    `java-library`
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21

java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
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

tasks.jar {
    from(rootProject.file("LICENSE.txt")) {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}
