import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room3)
}

val targetAndroidSdk = 35

val keystorePropertiesFile = File(System.getProperty("user.home"), ".dontspill-secrets/keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) keystorePropertiesFile.inputStream().use { load(it) }
}
val hasReleaseKeystore = keystorePropertiesFile.exists()

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

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
            if (hasReleaseKeystore) signingConfig = signingConfigs.getByName("release")
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

// 서명 키가 없으면 서명되지 않은 release APK가 만들어지지 않게 release 빌드를 멈춘다. debug 빌드는 그대로 된다.
val checkReleaseKeystore = tasks.register("checkReleaseKeystore") {
    val keystoreFound = hasReleaseKeystore
    val keystorePath = keystorePropertiesFile.path
    doLast {
        if (!keystoreFound) throw GradleException("release 서명 정보가 없습니다: $keystorePath")
    }
}
tasks.matching { it.name == "preReleaseBuild" }.configureEach { dependsOn(checkReleaseKeystore) }

// Room DB 스키마를 JSON으로 남긴다. 버전을 올릴 때 마이그레이션을 비교하는 기준이다.
room3 {
    schemaDirectory("$projectDir/schemas")
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

    // Room
    implementation(libs.androidx.room3.runtime)
    ksp(libs.androidx.room3.compiler)

    // QR
    implementation(libs.zxing.core)

    // Pleos SDK
    implementation(libs.pleos.vehicle)
    implementation(libs.pleos.fused.location)
    implementation(libs.pleos.navi.helper)
}
