import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.androidMultiplatformKotlinLibrary)
    alias(libs.plugins.apollo)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

apollo {
    service("ShikiApp") {
        srcDir("src/commonMain/graphql/shikiapp")
        schemaFiles.from(file("src/commonMain/graphql/shikiapp/schema.graphqls"))
        packageName.set("org.application.shikiapp.generated.shikiapp")
        codegenModels.set("responseBased")
        warnOnDeprecatedUsages = true
        generateApolloMetadata = false
        generateOptionalOperationVariables = false
    }

    service("DarkShiki") {
        srcDir("src/commonMain/graphql/darkshiki")
        schemaFiles.from("src/commonMain/graphql/darkshiki/schema.graphqls")
        packageName.set("org.application.shikiapp.generated.darkshiki")
        codegenModels.set("responseBased")
        warnOnDeprecatedUsages = true
        generateApolloMetadata = false
        generateOptionalOperationVariables = false
    }
}

kotlin {
    android {
        namespace = "org.application.shikiapp.shared"
        minSdk = 26
        compileSdk = 36

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }

        androidResources.enable = true
    }

    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

    jvm()

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
                implementation(libs.compose.material3.navigationSuite)

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
                implementation(libs.bundles.ktor)
                implementation(libs.apollo.api)

                // Utils
                implementation(libs.coil.compose)
                implementation(libs.coil.network)
                implementation(libs.icu4j)
                implementation(libs.ksoup)
                implementation(libs.material.preferences)
                implementation(libs.zoomable)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.ktor.client.android)
            }
        }

        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)

                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.ktor.client.java)
            }
        }
    }
}

compose {
    desktop {
        application {
            mainClass = "MainKt"

            buildTypes.release.proguard {
                isEnabled.set(false)
            }

            jvmArgs += listOfNotNull(
                "-Xms512m",
                "-Xmx2048m",

                "-XX:+UseZGC",
                "-XX:+ZGenerational",
                "-XX:+UseStringDeduplication",

                "-Dskiko.vsync.enabled=true",
                "-Dskiko.fps.limit=120"
            )

            nativeDistributions {
                packageName = "ShikiApp"
                packageVersion = "0.6.0"

                targetFormats(TargetFormat.AppImage, TargetFormat.Exe)

                modules(
                    "java.net.http",
                    "java.logging",
                    "jdk.crypto.ec",
                    "jdk.crypto.cryptoki",
                    "java.naming",
                    "java.sql",
                    "java.management",
                    "jdk.unsupported"
                )

                windows {
                    iconFile.set(project.file("src/commonMain/composeResources/drawable/icon.ico"))
                }

                linux {
                    shortcut = true
                    appCategory = "Multimedia"
                    menuGroup = "Multimedia"
                    iconFile.set(project.file("src/commonMain/composeResources/drawable/icon.png"))
                }
            }
        }
    }
}

tasks.register<Zip>("packageZipDistributable") {
    group = "compose desktop"
    description = "Create .zip archive"

    dependsOn("createReleaseDistributable")

    val appName = "ShikiApp"
    val buildDir = layout.buildDirectory.get().asFile

    val appImageDir = file("$buildDir/compose/binaries/main-release/app/$appName")

    from(appImageDir) {
        into(appName)
    }

    destinationDirectory.set(file("$buildDir/distributions"))
    archiveFileName.set("$appName-portable.zip")

    doLast {
        println("Archive is ready at: ${destinationDirectory.get()}/${archiveFileName.get()}")
    }
}

val generateLanguagesList by tasks.registering {
    val resDir = file("src/commonMain/composeResources")
    val outputFile = file("src/commonMain/kotlin/GeneratedLanguages.kt")

    inputs.dir(resDir)
    outputs.file(outputFile)

    doLast {
        val languages = resDir.listFiles()
            ?.filter { it.isDirectory && it.name.startsWith("values-") }
            ?.map { it.name.substringAfter("values-") }
            ?.plus("ru")
            ?.distinct()
            ?: emptyList()

        outputFile.writeText("""
            object AppLanguages {
                val list = listOf(${languages.joinToString { "\"$it\"" }})
            }
        """.trimIndent())
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(generateLanguagesList)
}