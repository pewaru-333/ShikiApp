plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformKotlinLibrary)
    alias(libs.plugins.apollo)
}

apollo {
    service("ShikiApp") {
        srcDir("src/commonMain/graphql")
        schemaFiles.from(rootProject.file("composeApp/src/commonMain/graphql/shared/schema.graphqls"))
        packageName.set("org.application.shikiapp.generated.shikiapp")
        codegenModels.set("responseBased")
        issueSeverity("DeprecatedUsage", "ignore")
        generateApolloMetadata = false
        generateOptionalOperationVariables = false
    }
}

kotlin {
    android {
        namespace = "org.application.shikiapp.backend.shiki"
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
