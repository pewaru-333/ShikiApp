import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinJetbrainsJvm)
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.compose.resources)
    implementation(compose.desktop.currentOs)

    implementation(libs.kotlinx.coroutines.swing)

    // Coil
    implementation(libs.coil.compose)
}

val appName = project.findProperty("userAgent")
    .toString()
    .let { if (it == "DarkShiki") "ShikiRip" else it }

compose.desktop {
    application {
        mainClass = "org.application.shikiapp.shared.MainKt"

        buildTypes.release.proguard {
            isEnabled.set(false)
        }

        nativeDistributions {
            packageName = appName
            packageVersion = project.findProperty("APP_VERSION_NAME").toString()
                .substringAfterLast('-')

            appResourcesRootDir.set(project.layout.projectDirectory.dir("files"))

            targetFormats(TargetFormat.AppImage, TargetFormat.Exe)

            jvmArgs(
                "-Dapp.userAgent=$appName",

                "-Xms512m",
                "-Xmx2048m",

                "-XX:+AlwaysPreTouch",
                "-XX:+UseZGC",
                "-XX:+UseStringDeduplication",
                "-XX:+UseCompactObjectHeaders",

                "-XX:+TieredCompilation",
                "-XX:+SegmentedCodeCache",
                "-Xshare:auto",

                "--enable-native-access=ALL-UNNAMED",
                "-Dfile.encoding=UTF-8",

                "-Dskiko.vsync.enabled=true",
                "-Dskiko.gpu.resourceCacheLimit=1073741824" // 1GB VRAM cache limit
            )
            modules(
                "java.logging",
                "java.net.http",
                "jdk.crypto.cryptoki",
                "jdk.crypto.ec",
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
    }
}

tasks.register<Zip>("packageZipDistributable") {
    group = "compose desktop"
    description = "Create .zip archive for $appName"

    dependsOn("createReleaseDistributable")

    val buildDir = layout.buildDirectory.get().asFile

    val appImageDir = file("$buildDir/compose/binaries/main-release/app/$appName")
    from(appImageDir) {
        into(appName)
    }

    val resourcesDir = project.layout.projectDirectory.dir("files").asFile
    from(resourcesDir) {
        into("$appName/app/resources")
    }

    destinationDirectory.set(file("$buildDir/distributions"))
    archiveFileName.set("$appName-windows-portable.zip")

    doLast {
        println("Archive is ready at: ${destinationDirectory.get()}/${archiveFileName.get()}")
    }
}