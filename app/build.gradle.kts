
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.aprax.browser"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aprax.browser"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    // Fix: Java and Kotlin must use the same JVM target.
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Match Kotlin bytecode target to Java 17.
    kotlinOptions {
        jvmTarget = "17"
    }
}
