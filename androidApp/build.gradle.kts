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
        versionCode = project.findProperty("APP_VERSION_CODE").toString().toInt()
        versionName = project.findProperty("APP_VERSION_NAME") as String

        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a"))
        }
    }

    productFlavors {
        create("ShikiApp") {
            dimension = "version"
            applicationId = "org.application.shikiapp"
        }

        create("DarkShiki") {
            dimension = "version"
            applicationId = "rip.shikimori.app"
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
            optimization {
                enable = true
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

dependencies {
    // Plugin
    implementation(projects.composeApp)
    add("ShikiAppImplementation", projects.backendShiki)
    add("DarkShikiImplementation", projects.backendDark)
    add("DarkShikiImplementation", projects.yggdrasil)

    // Android
    implementation(project.dependencies.platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
}