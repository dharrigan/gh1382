import org.gradle.api.tasks.testing.logging.TestLogging
import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.2.71"
    val nebulaKotlinVersion = "1.2.71"
    val nebulaProjectVersion = "5.1.2"
    val springDependencyManagementVersion = "1.0.6.RELEASE"

    idea
    java
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("io.spring.dependency-management") version springDependencyManagementVersion
    id("nebula.kotlin") version nebulaKotlinVersion
    id("nebula.project") version nebulaProjectVersion
}

val nexusUrl: String by project

val commonsLangVersion = "3.7"
val jacksonVersion = "2.9.7"
val kotlinLoggingVersion = "1.6.10"
val springCloudStreamVersion = "Fishtown.BUILD-SNAPSHOT"
val springCloudVersion = "Finchley.SR1"

val snippetsDir = file("build/generated-snippets")

ext {
    set("jackson.version", jacksonVersion)
}

group = "com.dharrigan.gh1382"
version = System.getenv("PROJECT_VERSION") ?: "0.0.0-SNAPSHOT"
description = "GH1382"

java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenLocal()
    maven("$nexusUrl/maven-public")
}

with(configurations) {
    all { resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS) }
    testImplementation.exclude(module = "junit", group = "junit")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        mavenBom("org.springframework.cloud:spring-cloud-stream-dependencies:$springCloudStreamVersion")
    }
}

dependencies {
    implementation(kotlin("reflect"))

    // Custom Build using the GH-1382 branch
    implementation("org.springframework.cloud:spring-cloud-stream:2.1.0.BUILD-SNAPSHOT")

    // Latest from Spring Central
    implementation("org.springframework.boot:spring-boot:2.1.0.BUILD-SNAPSHOT")
    implementation("org.springframework.boot:spring-boot-starter-json:2.1.0.BUILD-SNAPSHOT")
    implementation("org.springframework.integration:spring-integration-core:5.1.0.RC1")

    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:$jacksonVersion")

    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("org.apache.commons:commons-lang3:$commonsLangVersion")

}

idea {
    module.isDownloadJavadoc = true
    project {
        vcs = "Git"
        languageLevel = IdeaLanguageLevel(java.sourceCompatibility)
    }
}

with(tasks) {
    withType<JavaCompile> {
        with(options) {
            encoding = "UTF-8"
            compilerArgs = mutableListOf("-Xlint", "-parameters")
            setIncremental(true)
        }
    }
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            javaParameters = true
            jvmTarget = java.sourceCompatibility.name
        }
    }
}

