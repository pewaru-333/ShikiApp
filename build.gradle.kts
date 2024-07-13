// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.apolloGraphQL) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.googleKsp) apply false
    alias(libs.plugins.jetbrainsKotlin) apply false
}