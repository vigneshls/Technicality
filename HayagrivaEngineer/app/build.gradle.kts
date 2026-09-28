plugins {
    id("com.android.application")
}

android {
    namespace = "com.hayagriva.engineer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hayagriva.engineer"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
