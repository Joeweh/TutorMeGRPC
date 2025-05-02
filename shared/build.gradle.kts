import com.google.protobuf.gradle.id

plugins {
    id("java-library")
    id("com.google.protobuf") version "0.9.5"
}

group = "io.github.joeweh.grpc"
version = "1.0-SNAPSHOT"

dependencies {

    implementation("com.google.protobuf:protobuf-java:4.30.2")
    api("com.google.protobuf:protobuf-kotlin:4.30.2")

    implementation("io.grpc:grpc-stub:1.72.0")
    api("io.grpc:grpc-kotlin-stub:1.4.3")

    api("io.grpc:grpc-api:1.72.0")
    implementation("io.grpc:grpc-protobuf:1.72.0")
    implementation("io.grpc:grpc-netty-shaded:1.72.0")

    if (JavaVersion.current().isJava9Compatible) {
        // Workaround for @javax.annotation.Generated
        // see: https://github.com/grpc/grpc-java/issues/3633
        implementation("javax.annotation:javax.annotation-api:1.3.2")
    }

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

protobuf {
    // Configure the protoc executable
    protoc {
        // Download from repositories
        artifact = "com.google.protobuf:protoc:4.30.2"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.72.0"
        }

        id("grpckotlin") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.3:jdk8@jar"
        }
    }

    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
                id("grpckotlin")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}