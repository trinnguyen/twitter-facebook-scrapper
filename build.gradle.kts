import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}
group = "com.trinnguyen"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://kotlin.bintray.com/kotlinx")
}
dependencies {
    testImplementation(kotlin("test-junit5"))
    implementation("org.seleniumhq.selenium:selenium-firefox-driver:3.141.59")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("commons-cli:commons-cli:1.4")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}