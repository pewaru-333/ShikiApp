plugins {
    alias(libs.plugins.kotlinMultiplatform)
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
        implementation(libs.apollo.api)
        implementation(libs.kotlin.library)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.compose.resources)
        implementation(libs.compose.ui)
        implementation(libs.androidx.paging.compose)
    }
}
