plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ru.example.fonarik"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.example.fonarik"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
