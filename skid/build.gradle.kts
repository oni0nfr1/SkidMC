import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("fabric-loom")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String
evaluationDependsOn(":skid-api")
val skidApiSourceSets = project(":skid-api").extensions.getByType<SourceSetContainer>()

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
        register("skid") {
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

kotlin.target.compilations.named("clientTest") {
    associateWith(kotlin.target.compilations.getByName("client"))
}


repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    implementation(project(path = ":skid-api", configuration = "namedElements"))
    add("clientImplementation", skidApiSourceSets.named("client").get().output)
    include(project(":skid-api"))

    add(clientTestSourceSet.implementationConfigurationName, kotlin("test-junit5"))
    add(
        clientTestSourceSet.runtimeOnlyConfigurationName,
        "org.junit.platform:junit-platform-launcher",
    )
}

val clientTest by tasks.registering(Test::class) {
    description = "Runs tests for the client implementation source set."
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
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to project.version,
                "minecraft_version" to project.property("minecraft_version"),
                "loader_version" to project.property("loader_version"),
                "kotlin_loader_version" to project.property("kotlin_loader_version")
            )
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}
