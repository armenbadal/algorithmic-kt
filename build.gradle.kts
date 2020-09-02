import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.apache.bcel:bcel:6.5.0")
}

application {
    mainClassName = "algorithmic.main.MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}
