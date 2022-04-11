import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.6.20"
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.bcel:bcel:6.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
}

application {
    mainClass.set("algorithmic.main.MainKt")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        csv.isEnabled = false
        html.isEnabled = true
        html.destination = File("${buildDir}/jacoco/html")
    }
}
