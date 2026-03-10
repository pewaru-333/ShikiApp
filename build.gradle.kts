plugins {
    // Android
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidMultiplatformKotlinLibrary) apply false

    // JetBrains / Compose
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false

    // Kotlin
    alias(libs.plugins.kotlinJetbrainsJvm) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    // Apollo
    alias(libs.plugins.apollo) apply false
}