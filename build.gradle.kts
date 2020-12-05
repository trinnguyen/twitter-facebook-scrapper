
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
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    implementation("org.seleniumhq.selenium:selenium-firefox-driver:3.141.59")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:3.141.59")

    implementation("org.jsoup:jsoup:1.13.1")
    implementation("commons-cli:commons-cli:1.4")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation("org.twitter4j:twitter4j-core:4.0.7")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}
tasks.withType<Test> {
    useJUnitPlatform()
}
