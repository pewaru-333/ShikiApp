plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.apollo)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
}

apollo {
    service("service") {
        packageName.set("org.application")
        codegenModels.set("responseBased")
        generateOptionalOperationVariables = false
    }
}

android {
    namespace = "org.application.shikiapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.application.shikiapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 19
        versionName = "alpha-0.2.4"
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ============================== Android ==============================
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.paging)

    // ============================== Network ==============================
    implementation(libs.apollo.engine.ktor)
    implementation(libs.apollo.runtime)
    implementation(libs.bundles.ktor)

    // ============================== Navigation ==============================
    implementation(libs.androidx.navigation)
    implementation(libs.kotlinx.serialization.json)

    // ============================== Utilities ==============================
    implementation(libs.coil.compose)
    implementation(libs.material.preferences)
}