plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.apollo)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

apollo {
    service("service") {
        packageName.set("org.application.shikiapp.generated")
        codegenModels.set("responseBased")
        warnOnDeprecatedUsages = true
        generateApolloMetadata = false
        generateOptionalOperationVariables = false
    }
}

android {
    namespace = "org.application.shikiapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.application.shikiapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 29
        versionName = "alpha-0.4.4"
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    androidResources {
        generateLocaleConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        }
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ============================== Android ==============================
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.paging)

    // ============================== Network ==============================
    implementation(libs.bundles.ktor)
    implementation(libs.apollo.api)

    // ============================== Navigation ==============================
    implementation(libs.androidx.navigation)
    implementation(libs.kotlinx.serialization.json)

    // ============================== Utilities ==============================
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.network)
    implementation(libs.coil.zoomable)
    implementation(libs.material.preferences)
    implementation(libs.jsoup)
}