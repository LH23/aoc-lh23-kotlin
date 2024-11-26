plugins {
    kotlin("jvm") version "2.0.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation(kotlin("reflect"))
    implementation("tools.aqua:z3-turnkey:4.12.2.1")
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "8.11"
    }
}
