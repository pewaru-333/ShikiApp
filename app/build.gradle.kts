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
    compileSdk = 35

    defaultConfig {
        applicationId = "org.application.shikiapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 21
        versionName = "alpha-0.3.0"
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
        jvmTarget = JavaVersion.VERSION_17.toString()
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
}