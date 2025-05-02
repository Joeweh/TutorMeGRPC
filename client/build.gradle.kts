plugins {
    id("java")
}

group = "io.github.joeweh.grpc"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":shared"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}