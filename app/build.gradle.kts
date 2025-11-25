import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val envProperties = Properties().apply {
    val envFile = rootProject.file(".env")
    if (envFile.exists()) {
        envFile.inputStream().use { load(it) }
    }
}

val mapsApiKey: String = System.getenv("MAPS_API_KEY")
    ?: envProperties.getProperty("MAPS_API_KEY")
    ?: (project.findProperty("MAPS_API_KEY") as? String)
    ?: ""

android {
    namespace = "com.example.bghelp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.bghelp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        if (mapsApiKey.isBlank()) {
            logger.warn("MAPS_API_KEY is not set. Google Maps may not render correctly.")
        }
        resValue("string", "google_maps_key", mapsApiKey)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

configurations.all {
    resolutionStrategy {
        force("androidx.vectordrawable:vectordrawable:1.2.0")
        force("androidx.vectordrawable:vectordrawable-animated:1.2.0")
    }
}

dependencies {
    // Compose BOM controls all Compose versions
    implementation(platform(libs.androidx.compose.bom))

    // UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)

    // Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.foundation)
    ksp(libs.androidx.room.compiler)

    // Gson
    implementation(libs.gson)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // DateTimePicker
    implementation(libs.material.dialogs.datetime)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // AndroidX core + lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Accompanist System UI Controller
    implementation(libs.accompanist.systemuicontroller)

    // Maps & Places
    implementation(libs.play.services.maps)
    implementation(libs.google.maps.compose)
    implementation(libs.places)
    implementation(libs.coroutines.play.services)

    // Color picker (skydoves/colorpicker-compose)
    implementation(libs.colorpicker.compose)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}