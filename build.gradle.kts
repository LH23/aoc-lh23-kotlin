plugins {
    kotlin("jvm") version "2.0.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    implementation("tools.aqua:z3-turnkey:4.12.2.1")
    implementation(kotlin("reflect"))
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0-M1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0-M1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0-M1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.0-M1")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.8.22")
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }
}
// improve Day error print in console
tasks.withType<JavaExec> {
    setIgnoreExitValue(true)
}