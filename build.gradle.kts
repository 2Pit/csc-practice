import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
val exposed_version: String by project

plugins {
    application
    kotlin("jvm") version "1.3.31"
}

group = "com.example"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.eclipse.mylyn.github:org.eclipse.egit.github.core:2.1.5")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("ch.qos.logback:logback-classic:$logback_version")
    compile("io.ktor:ktor-server-core:$ktor_version")
    compile("io.ktor:ktor-auth:$ktor_version")
    compile("io.ktor:ktor-auth-jwt:$ktor_version")
    compile("io.ktor:ktor-jackson:$ktor_version")
    compile("io.ktor:ktor-client-apache:$ktor_version")
    compile("org.jetbrains.exposed:exposed:$exposed_version")
    testCompile("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}