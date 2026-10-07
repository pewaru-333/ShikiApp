plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformKotlinLibrary)
    alias(libs.plugins.kotlinSerialization)
}

extra["kotlin.mpp.enableCInteropCommonization"] = "true"

kotlin {
    android {
        namespace = "org.application.shikiapp.yggdrasil"

        compileSdk = 37
        minSdk = 26

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        }

    }

    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    val framework = rootProject.file("composeApp/libs/ios/Yggbridge.xcframework")
    if (System.getProperty("os.name").startsWith("Mac", ignoreCase = true)) {
        listOf(
            targets.getByName("iosArm64") to "ios-arm64",
            targets.getByName("iosSimulatorArm64") to "ios-arm64_x86_64-simulator",
        ).forEach { (target, slice) ->
            (target as org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget)
                .compilations.getByName("main").cinterops.create("Yggbridge") {
                    definitionFile.set(project.file("src/nativeInterop/cinterop/Yggbridge.def"))
                    compilerOpts("-fmodules", "-framework", "Yggbridge", "-F$framework/$slice")
                }
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.networkApi)
            implementation(libs.kotlin.library)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(project(":yggdrasil:android-bridge"))
        }

        jvmMain.dependencies {
            implementation(libs.jna)
        }
    }
}
