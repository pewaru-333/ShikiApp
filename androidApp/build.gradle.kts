plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    flavorDimensions += "version"
    namespace = "org.application.shikiapp"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        targetSdk = 37
        versionCode = 51
        versionName = "alpha-0.7.3"

        buildFeatures {
            buildConfig = true
        }

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    productFlavors {
        create("ShikiApp") {
            dimension = "version"
            applicationId = "org.application.shikiapp"

            manifestPlaceholders["authScheme"] = "app"
            manifestPlaceholders["authHost"] = "login"
            manifestPlaceholders["authSuffix"] = ""
            manifestPlaceholders["base"] = "shikimori.io"
            manifestPlaceholders["mirrorOne"] = "shikimori.one"
            manifestPlaceholders["mirrorTwo"] = "shiki.one"

            buildConfigField("String", "USER_AGENT", "\"ShikiApp\"")
        }

        create("DarkShiki") {
            dimension = "version"
            applicationId = "org.application.darkshiki"

            manifestPlaceholders["authScheme"] = "darkshiki"
            manifestPlaceholders["authHost"] = "auth"
            manifestPlaceholders["authSuffix"] = "login"
            manifestPlaceholders["base"] = "shikimori.rip"
            manifestPlaceholders["mirrorOne"] = "shikimori.moe"
            manifestPlaceholders["mirrorTwo"] = "shikimori.net"

            buildConfigField("String", "USER_AGENT", "\"DarkShiki\"")
        }
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    // Plugin
    implementation(projects.composeApp)

    // Android
    implementation(project.dependencies.platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
}