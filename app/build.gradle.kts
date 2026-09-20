plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.app.assistant"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aistudio.assistant.apidkv"
        minSdk = 24
        targetSdk = 36
        val runNumber = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 0
        versionCode = 2 + runNumber
        versionName = if (runNumber > 0) "2.1.$runNumber" else "2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val keystoreFilePath = System.getenv("KEYSTORE_FILE_PATH")
    val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
    val keystoreKeyAlias = System.getenv("KEYSTORE_KEY_ALIAS")
    val keystoreKeyPassword = System.getenv("KEYSTORE_KEY_PASSWORD")

    val hasSigningConfig = !keystoreFilePath.isNullOrEmpty() &&
            !keystorePassword.isNullOrEmpty() &&
            !keystoreKeyAlias.isNullOrEmpty() &&
            !keystoreKeyPassword.isNullOrEmpty()

    signingConfigs {
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        if (hasSigningConfig) {
            create("release") {
                storeFile = file(keystoreFilePath)
                storePassword = keystorePassword
                keyAlias = keystoreKeyAlias
                keyPassword = keystoreKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true // Ensure buildConfig is enabled
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.size)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.mlkit.translate)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.commonmark)
    //implementation ("com.github.jeziellago:compose-markdown:0.5.4")
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.play.services.location)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.runtime.saveable)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //MediaPipe library
    implementation(libs.mediapipe.tasks.text)

    // Sherpa ONNX Offline STT
    implementation(files("libs/sherpa-onnx-1.13.2.aar"))

    // CameraX dependencies
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
}

secrets {
    propertiesFileName = ".env"
    defaultPropertiesFileName = ".env.example"
}
