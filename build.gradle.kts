import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}
group = "com.trinnguyen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://kotlin.bintray.com/kotlinx")
}
dependencies {
    testImplementation(kotlin("test-junit5"))
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:3.141.59")
    implementation("org.seleniumhq.selenium:selenium-firefox-driver:4.0.0-alpha-7")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("commons-cli:commons-cli:1.4")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}
tasks.withType<ShadowJar> {
    baseName = "app"
    classifier = ""
    version = ""
}