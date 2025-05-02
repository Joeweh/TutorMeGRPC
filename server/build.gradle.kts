plugins {
    id("java")
}

group = "io.github.joeweh.grpc"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":shared"))

    implementation("com.mysql:mysql-connector-j:9.3.0")

    implementation("com.zaxxer:HikariCP:6.3.0")

    implementation("org.mindrot:jbcrypt:0.4")

    implementation("ch.qos.logback:logback-classic:1.5.18")

    implementation("org.eclipse.angus:jakarta.mail:2.0.3")

    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-css:2025.4.17")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}