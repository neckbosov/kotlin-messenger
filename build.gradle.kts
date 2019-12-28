import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile

val vKtor: String by project
val vExposed: String by project
val vKoin: String by project
val vPostgres: String by project
val vKotlinx: String by project
val vTestcontainers: String by project
val vKotlintest: String by project

plugins {
    id("com.bmuschko.docker-remote-api") version "6.0.0"
    kotlin("jvm")
    application
    java
}

group = "com.neckbosov"
version = "0.0.1"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven(url = "https://kotlin.bintray.com/ktor")
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$vKotlinx")

    // Test
    testImplementation("io.kotlintest:kotlintest-runner-junit5:$vKotlintest")
    testImplementation("org.testcontainers:junit-jupiter:$vTestcontainers")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$vKotlinx")

    // Koin
    implementation("org.koin:koin-ktor:$vKoin")
    implementation("org.koin:koin-logger-slf4j:$vKoin")
    implementation("org.koin:koin-core:$vKoin")
    testImplementation("org.koin:koin-test:$vKoin")
    testImplementation("io.kotlintest:kotlintest-extensions-koin:$vKotlintest")

    // Ktor
    implementation("io.ktor:ktor-server-netty:$vKtor")
    implementation("io.ktor:ktor-server-core:$vKtor")
    implementation("io.ktor:ktor-auth:$vKtor")
    implementation("io.ktor:ktor-auth-jwt:$vKtor")
    implementation("io.ktor:ktor-jackson:$vKtor")
    implementation("io.ktor:ktor-client-apache:$vKtor")
    implementation("io.ktor:ktor-client-json-jvm:$vKtor")
    implementation("io.ktor:ktor-client-jackson:$vKtor")
    implementation("io.ktor:ktor-websockets:$vKtor")
    testImplementation("io.ktor:ktor-server-tests:$vKtor")
    testImplementation("io.ktor:ktor-server-test-host:$vKtor")

    // Exposed
    implementation("org.jetbrains.exposed:exposed-core:$vExposed")
    implementation("org.jetbrains.exposed:exposed-dao:$vExposed")
    implementation("org.jetbrains.exposed:exposed-jdbc:$vExposed")
    implementation("org.jetbrains.exposed:exposed-jodatime:$vExposed")

    // Database
    implementation("org.postgresql:postgresql:$vPostgres")
    implementation("com.zaxxer:HikariCP:3.4.1")
    testImplementation("org.testcontainers:postgresql:$vTestcontainers")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}

val createDockerfile by tasks.creating(Dockerfile::class) {
    dependsOn(tasks.installDist)
    destFile.set(file("docker/app/Dockerfile"))
    from("postgres:alpine")
    copyFile("/build/install/SnailMail", "/opt/SnailMail")
    runCommand("apk update && apk add openjdk11-jre nginx")
    exposePort(5432)
//    entryPoint("/opt/SnailMail/bin/SnailMail")
}

tasks.create("buildImage", DockerBuildImage::class) {
    dockerFile.set(file("docker/app/Dockerfile"))
    inputDir.set(file("."))
    images.add("snailmail:latest")
}