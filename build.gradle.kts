import Build_gradle.Constants.arrow_version
import Build_gradle.Constants.exposed_version
import Build_gradle.Constants.kotlin_version
import Build_gradle.Constants.ktor_version
import Build_gradle.Constants.logback_version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Constants {
    const val kotlin_version: String = "1.3.40"
    const val logback_version: String = "1.2.3"
    const val ktor_version: String = "1.2.2"
    const val exposed_version: String = "0.13.2"
    const val arrow_version: String = "0.9.0"
}


plugins {
    application
    kotlin("jvm") version "1.3.40"
    kotlin("kapt") version "1.3.40"
    id("kotlinx-serialization") version "1.3.40"
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
    maven { url = uri("https://dl.bintray.com/arrow-kt/arrow-kt/") }
    maven { url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local/") }
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.eclipse.mylyn.github:org.eclipse.egit.github.core:2.1.5")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    compile("org.jetbrains.kotlin:kotlin-script-runtime:$kotlin_version")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("ch.qos.logback:logback-classic:$logback_version")
    compile("io.ktor:ktor-server-core:$ktor_version")
    compile("io.ktor:ktor-auth:$ktor_version")
    compile("io.ktor:ktor-auth-jwt:$ktor_version")
    compile("io.ktor:ktor-jackson:$ktor_version")
    compile("io.ktor:ktor-client-apache:$ktor_version")
    compile("org.jetbrains.exposed:exposed:$exposed_version")

    compile("io.arrow-kt:arrow-core-data:$arrow_version")
    compile("io.arrow-kt:arrow-core-extensions:$arrow_version")
    compile("io.arrow-kt:arrow-syntax:$arrow_version")
    compile("io.arrow-kt:arrow-typeclasses:$arrow_version")
    compile("io.arrow-kt:arrow-extras-data:$arrow_version")
    compile("io.arrow-kt:arrow-extras-extensions:$arrow_version")
    kapt("io.arrow-kt:arrow-meta:$arrow_version")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.1")

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