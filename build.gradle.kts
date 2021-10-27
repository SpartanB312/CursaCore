group = "net.spartanb312"
version = "1.0.0"

plugins {
    java
    kotlin("jvm")
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://mvnrepository.com/artifact/")
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.8")
}

tasks {

    jar {
        archiveBaseName.set(project.name.toLowerCase())
    }

    val sourceJar by register<Jar>("sourcesJar") {
        group = "build"
        from(sourceSets.main.get().allSource)

        archiveBaseName.set(project.name.toLowerCase())
        archiveClassifier.set("sources")
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = project.name.toLowerCase()

                from(project.components["kotlin"])
                artifact(sourceJar)
            }
        }
    }

}