plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.apolloGraphQL)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleKsp)
    alias(libs.plugins.jetbrainsKotlin)
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
        versionCode = 13
        versionName = "alpha-0.1.10"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.apollo.runtime)
    implementation(libs.converter.moshi)
    implementation(libs.retrofit)
    implementation(platform(libs.okhttp.bom))

    // ============================== Navigation ==============================
    ksp(libs.compose.destinations.ksp)
    implementation(libs.compose.destinations)

    // ============================== Utilities ==============================
    implementation(libs.coil.compose)
    implementation(libs.preference.library)
}