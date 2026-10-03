plugins {
    alias(libs.plugins.androidMultiplatformKotlinLibrary)
    alias(libs.plugins.apollo)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

apollo {
    service("Common") {
        srcDir("src/commonMain/graphql/shared")
        schemaFiles.from(file("src/commonMain/graphql/shared/schema.graphqls"))
        packageName.set("org.application.shikiapp.generated.common")
        codegenModels.set("responseBased")
        issueSeverity("DeprecatedUsage", "ignore")
        generateApolloMetadata = false
        generateOptionalOperationVariables = false
    }
}

kotlin {
    android {
        namespace = "org.application.shikiapp.shared"
        minSdk = 26
        compileSdk = 37

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        }

        androidResources {
            enable = true
        }
    }

    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

    applyDefaultHierarchyTemplate()

    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                // Compose
                implementation(libs.compose.runtime)
                implementation(libs.compose.resources)
                implementation(libs.compose.ui)

                // Material Design & Adaptive
                implementation(libs.compose.material3)
                implementation(libs.compose.material3.adaptive)
                implementation(libs.compose.material3.navigation.suite)

                // Kotlin
                implementation(libs.kotlin.library)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)

                // Lifecycle, Navigation, Paging
                implementation(libs.compose.lifecycle.runtime)
                implementation(libs.compose.lifecycle.viewmodel)
                implementation(libs.compose.navigation)
                implementation(libs.compose.navigation.event)
                implementation(libs.androidx.paging.compose)

                // Network
                api(projects.networkApi)
                api(libs.ktor.client.engines.defaults)
                api(libs.apollo.api)
                implementation(libs.bundles.ktor)

                // Utils
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)
                implementation(libs.kotlinx.datetime)
                implementation(libs.ksoup)
                implementation(libs.zoomable)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.activity.compose)

                implementation(libs.bundles.media3)
                implementation(libs.libass) // Subtitles (.ass)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)

                implementation(libs.kotlinx.coroutines.swing)

                // Internalization
                implementation(libs.icu4j)

                // Video player
                implementation(libs.vlcj)
                implementation(libs.jna) // VLC and Yggdrasil
            }
        }
    }
}

compose.resources {
    publicResClass = true
}