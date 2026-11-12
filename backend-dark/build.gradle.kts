plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidMultiplatformKotlinLibrary)
    alias(libs.plugins.apollo)
}

apollo {
    service("DarkShiki") {
        srcDir("src/commonMain/graphql")
        schemaFiles.from(file("src/commonMain/graphql/schema.graphqls"))
        packageName.set("org.application.shikiapp.generated.darkshiki")
        codegenModels.set("responseBased")
        issueSeverity("DeprecatedUsage", "ignore")
        generateApolloMetadata = false
        generateOptionalOperationVariables = false
    }
}

kotlin {
    android {
        namespace = "org.application.shikiapp.backend.dark"
        compileSdk = 37
        minSdk = 26

        androidResources {
            enable = true
        }

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

    sourceSets.commonMain.dependencies {
        api(projects.composeApp)
        api(projects.yggdrasil)
        implementation(libs.compose.runtime)
        implementation(libs.compose.material3)
        implementation(libs.apollo.api)
        implementation(libs.kotlin.library)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.ktor.client.core)
        implementation(libs.compose.resources)
        implementation(libs.compose.ui)
        implementation(libs.androidx.paging.compose)
    }
}

compose.resources {
    packageOfResClass = "org.application.shikiapp.backend.dark.resources"
}