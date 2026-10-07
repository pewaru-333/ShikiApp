plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinJetbrainsJvm)
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation.layout)

    val os = System.getProperty("os.name")
    val arm64 = System.getProperty("os.arch") in listOf("aarch64", "arm64")
    val platform = when {
        os.startsWith("Windows") -> "win"
        os.startsWith("Mac") -> if (arm64) "mac-aarch64" else "mac"
        else -> if (arm64) "linux-aarch64" else "linux"
    }

    listOf(
        libs.javafx.base,
        libs.javafx.graphics,
        libs.javafx.controls,
        libs.javafx.media,
        libs.javafx.web,
        libs.javafx.swing
    ).forEach { dependency ->
        val module = dependency.get()
        implementation("${module.module}:${module.version}:$platform") {
            isTransitive = false
        }
    }
}
