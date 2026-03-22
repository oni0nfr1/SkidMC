plugins {
    kotlin("jvm")
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":skid-test-annotations"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.3.5")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
}
