import com.android.build.api.variant.BuildConfigField
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val targetAndroidSdk = 35

val debugFallbackBaseUrl = "https://example.invalid/"

// user.home 아래 비밀 파일 경로.
val secretsFile = File(System.getProperty("user.home"), ".chaduck-secrets/secrets.properties")

// 비밀 파일 내용을 Properties로 읽는다. 파일이 없으면 빈 Properties.
val secretProperties: Provider<Properties> =
    providers.fileContents(layout.projectDirectory.file(secretsFile.path)).asText
        .map { text -> Properties().apply { load(text.reader()) } }
        .orElse(Properties())

fun baseUrlProvider(key: String, fallback: String?): Provider<String> = secretProperties.map { properties ->
    val url = properties.getProperty(key) ?: fallback
        ?: throw GradleException("비밀 파일에 $key 값을 넣어 주세요. 예: $key=https://<서버 주소>/")
    if (!url.endsWith("/")) throw GradleException("비밀 파일의 $key 값은 /로 끝나야 합니다.")
    url
}

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

androidComponents {
    onVariants { variant ->
        val buildType = variant.buildType ?: return@onVariants
        val fallback = if (buildType == "debug") debugFallbackBaseUrl else null
        // 값 검사는 해당 빌드의 BuildConfig를 만들 때만 한다. debug 빌드가 release 값 때문에 실패하지 않게 한다.
        variant.buildConfigFields?.put(
            "CHADUCK_BASE_URL",
            baseUrlProvider("chaduck.baseUrl.$buildType", fallback).map {
                BuildConfigField("String", "\"$it\"", "서버 기본 주소")
            }
        )
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

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

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
}
