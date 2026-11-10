plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val targetAndroidSdk = 35

android {
    namespace = "com.yozmpeople.dontspill"
    compileSdk {
        version = release(targetAndroidSdk)
    }

    defaultConfig {
        applicationId = "com.yozmpeople.dontspill"
        minSdk = 28
        targetSdk = targetAndroidSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // CarPropertyManager, CarPowerManager 등 AAOS 표준 API
    useLibrary("android.car")

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    // JVM 단위 테스트에서 android.jar 메서드(Log.d 등)가 예외 대신 기본값을 돌려주게 한다.
    // debugLog가 상태 변화 때 Log.d를 불러도 판단 로직 테스트가 멈추지 않는다.
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.window.core)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    // Coroutines · Serialization
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // QR
    implementation(libs.zxing.core)

    // Pleos SDK
    implementation(libs.pleos.vehicle)
    implementation(libs.pleos.text.to.speech)
    implementation(libs.pleos.fused.location)
    implementation(libs.pleos.navi.helper)
}
