plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.apolloGraphQL)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrainsKotlin)
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
        versionCode = 15
        versionName = "alpha-0.1.15"
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
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.paging.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.kotlin.bom))

    // ============================== Network ==============================
    implementation(libs.apollo.engine.ktor)
    implementation(libs.apollo.runtime)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.serialization.kotlinx.json)

    // ============================== Navigation ==============================
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // ============================== Utilities ==============================
    implementation(libs.coil.compose)
    implementation(libs.preference.library)
}