plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.spotless)
    alias(libs.plugins.errorprone)
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.postgresql)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    //--- Обслуживание кода
    errorprone(libs.errorprone.core)
    //---
    //--- БД миграция
    implementation(libs.flyway.core)
    implementation(libs.flyway.database.postgresql)
    //---
}

tasks.test {
    useJUnitPlatform()
}