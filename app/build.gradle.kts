plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.0"
}

android {
    namespace = "com.example.plzgod"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.plzgod"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true // Compose 활성화
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0" // Compose와 호환되는 Kotlin Compiler Extension 버전
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(files("libs/tmap-sdk-1.6.aar"))
    implementation(files("libs/vsm-tmap-sdk-v2-android-1.7.23.aar"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Material 3 라이브러리 추가
    implementation("androidx.compose.material3:material3:1.1.1")

    // Compose dependencies
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    // Optional - For Compose debugging tools
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.0")
}