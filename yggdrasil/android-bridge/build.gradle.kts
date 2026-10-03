configurations.create("default") {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts.add("default", rootProject.file("composeApp/libs/android/yggbridge.aar"))
