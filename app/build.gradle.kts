plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)


}

android {
    namespace = "com.example.uni_project"
    compileSdk = 36
    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.uni_project"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material)
    implementation(libs.navigation.compose)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.benchmark.traceprocessor)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.material.icons.core)
    implementation(libs.material.icons.extended)

    implementation(libs.accompanist.permissions)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.coil.compose)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.security.crypto)


    implementation(libs.coroutines.android)
    implementation(libs.coroutines.play.services)

    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.ui.text)
    implementation(libs.material3)
    implementation(libs.protolite.well.known.types)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.ui)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.foundation.layout)
    ksp(libs.room.compiler)
    implementation(libs.play.services.auth)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}