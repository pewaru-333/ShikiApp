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
        generateOptionalOperationVariables = false
    }
}

android {
    namespace = "org.application.shikiapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.application.shikiapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "alpha-0.0.1"
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.startup.runtime)
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
    implementation(libs.coil.video)
    implementation(libs.preference.library)
}