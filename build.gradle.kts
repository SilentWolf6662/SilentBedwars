plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.necroservers.silentwolf"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") } // For GitHub snapshots
    maven { url = uri("https://repo.cloudburstmc.org/repository/maven-public/") } // PowerNukkitX deps
    maven { url = uri("https://repo.opencollab.dev/maven-releases") }  // For some transitive deps
    maven { url = uri("https://repo.opencollab.dev/maven-snapshots") } // Snapshot dependencies
}

dependencies {
    // Provided by the server
    compileOnly("com.github.PowerNukkitX:PowerNukkitX:master-SNAPSHOT")

    // JSON library
    implementation("org.json:json:20231013")

    // Unit testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("SilentBedwars")
    archiveVersion.set("1.0.0")
    archiveClassifier.set("") // no "-all"
}