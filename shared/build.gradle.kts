plugins {
    id("java")
    alias(libs.plugins.spotless)
    alias(libs.plugins.errorprone)
}

dependencies {
    //--- Обслуживание кода
    errorprone(libs.errorprone.core)
    //---
}