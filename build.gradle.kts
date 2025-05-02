plugins {
    kotlin("jvm") version "2.1.20"
}

group = "io.github.joeweh.grpc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

subprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}