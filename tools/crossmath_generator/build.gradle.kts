plugins {
    kotlin("jvm") version "2.2.21"
    application
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("GenerateLevelsKt")
}

tasks.named<JavaExec>("run") {
    // Output paths inside GenerateLevels.kt are relative to this directory.
    workingDir = projectDir
}
