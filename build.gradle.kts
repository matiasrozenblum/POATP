import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
    id("org.springframework.boot") version "2.4.5"
    jacoco
}

apply(plugin = "io.spring.dependency-management")

group = "com.mrozenblum"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11
val exposed_version = "0.29.1"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("mysql:mysql-connector-java:8.0.17")
    implementation("com.zaxxer:HikariCP:3.4.5")
    implementation("com.h2database:h2:1.4.200")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}