plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    val product = providers.environmentVariable("APP_USER_AGENT")
        .orElse(providers.gradleProperty("userAgent"))
        .orElse("DarkShiki")
        .get()

    val isShikiRip = product != "ShikiApp"
    val linkBridge = isShikiRip && providers.systemProperty("os.name")
        .map { it.startsWith("Mac", ignoreCase = true) }
        .get()

    listOf(
        iosArm64() to "ios-arm64",
        iosSimulatorArm64() to "ios-arm64_x86_64-simulator"
    ).forEach { (target, slice) ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true

            export(projects.composeApp)
            if (linkBridge) {
                val framework = rootProject.layout.projectDirectory.dir("composeApp/libs/ios/Yggbridge.xcframework")
                linkerOpts("-framework", "Yggbridge", "-F${framework.dir(slice).asFile.absolutePath}")
            }
        }
    }

    sourceSets.commonMain {
        val sourceSet = if (isShikiRip) "DarkShiki" else "ShikiApp"
        kotlin.srcDir("src/$sourceSet/kotlin")

        dependencies {
            api(projects.composeApp)
            if (isShikiRip) {
                implementation(projects.backendDark); implementation(projects.yggdrasil)
            } else {
                implementation(projects.backendShiki)
            }
            implementation(libs.compose.ui)
            implementation(libs.compose.runtime)
            implementation(libs.coil.compose)
            implementation(libs.kotlin.library)
        }
    }
}
