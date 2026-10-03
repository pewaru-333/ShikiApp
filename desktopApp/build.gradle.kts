import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.application.tasks.AbstractJPackageTask

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinJetbrainsJvm)
}
val isShikiRip = providers.gradleProperty("userAgent")
    .orElse("ShikiApp")
    .get() != "ShikiApp"
val appName = if (isShikiRip) "ShikiRip" else "ShikiApp"

kotlin {
    val sourceSet = if (isShikiRip) "DarkShiki" else "ShikiApp"

    jvmToolchain(25)
    sourceSets.named("main") {
        kotlin.srcDir("src/$sourceSet/kotlin")
    }
}

dependencies {
    implementation(projects.composeApp)
    if (isShikiRip) { implementation(projects.backendDark); implementation(projects.yggdrasil) }
    else implementation(projects.backendShiki)

    implementation(libs.compose.resources)
    implementation(compose.desktop.currentOs)

    implementation(libs.kotlinx.coroutines.swing)

    // Coil
    implementation(libs.coil.compose)
}

compose.desktop {
    application {
        val appResources = layout.buildDirectory.dir("productResources/$appName")
        val stageProductResources = tasks.register<Sync>("stageProductResources") {
            description = "Stage native resources for $appName by OS and architecture"
            from("files") {
                into("common")
                exclude("vlc/**", "yggbridge/**")
            }

            mapOf("windows-x64" to "win32-x86-64", "linux-x64" to "linux-x86-64").forEach { (target, vlcPlatform) ->
                from("files/vlc/$vlcPlatform") {
                    into("$target/vlc/$vlcPlatform")
                }

                if (isShikiRip) {
                    from("files/yggbridge/$target") {
                        into("$target/yggbridge/$target")
                    }
                }
            }

            into(appResources)
        }

        tasks.matching {
            it.name.startsWith("prepare") && it.name.endsWith("AppResources")
        }.configureEach {
            dependsOn(stageProductResources)
        }

        tasks.withType<AbstractJPackageTask>().configureEach {
            dependsOn(stageProductResources)
            inputs.dir(appResources)
                .withPropertyName("productResources")
                .withPathSensitivity(PathSensitivity.RELATIVE)
        }

        mainClass = "org.application.shikiapp.shared.MainKt"
        javaHome = javaToolchains
            .launcherFor { languageVersion.set(JavaLanguageVersion.of(25)) }
            .get()
            .metadata
            .installationPath.asFile.absolutePath

        jvmArgs(
            "-Xms128m",
            "-Xmx2048m",
            "-XX:+UseCompactObjectHeaders",
            "--enable-native-access=ALL-UNNAMED",
            "-Dfile.encoding=UTF-8"
        )

        buildTypes.release.proguard {
            isEnabled.set(false)
        }

        nativeDistributions {
            packageName = appName
            packageVersion = providers.gradleProperty("APP_VERSION_NAME")
                .map { it.substringAfterLast('-') }
                .get()

            appResourcesRootDir.set(appResources)

            targetFormats(TargetFormat.AppImage, TargetFormat.Exe)

            modules(
                "java.instrument",
                "java.management",
                "java.net.http",
                "jdk.localedata",
                "jdk.unsupported"
            )

            windows {
                val icon = if (appName == "ShikiApp") "src/main/resources/icons/icon.ico"
                else "src/main/resources/icons/icon_rip.ico"

                iconFile.set(project.file(icon))
            }

            linux {
                val icon = if (appName == "ShikiApp") "src/main/resources/icons/icon.png"
                else "src/main/resources/icons/icon_rip.png"

                iconFile.set(project.file(icon))
            }
        }

        tasks.register<Zip>("packageZipDistributable") {
            group = "compose desktop"
            description = "Create .zip archive for $appName"

            dependsOn("createReleaseDistributable")

            from(layout.buildDirectory.dir("compose/binaries/main-release/app/$appName")) {
                into(appName)
            }

            destinationDirectory.set(layout.buildDirectory.dir("distributions"))
            archiveFileName.set("$appName-windows-portable.zip")

            doLast {
                println("Archive is ready at: ${destinationDirectory.get()}/${archiveFileName.get()}")
            }
        }
    }
}