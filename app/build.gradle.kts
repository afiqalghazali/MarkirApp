plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-parcelize")
}

android {
    namespace = "com.scifi.markirapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.scifi.markirapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "\"AIzaSyASBn8-KqQuQzCmOWsnIAzXey-rT2dzQIo\"")
        buildConfigField(
            "String",
            "DIRECTIONS_API_KEY",
            "\"AIzaSyD4MtMBInFRcuhMs0FI4BkEttFXsGJN7ys\""
        )
        buildConfigField("String", "PLACES_API_KEY", "\"AIzaSyBxmj04Zn0YHrEt6uHkojiinLxFGsoQsPI\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.circleimageview)
    implementation(libs.play.services.location)
    implementation(libs.places)
    implementation(libs.glide)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.viewpager2)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.firebase.auth)
    implementation(libs.android.maps.utils)
    implementation(libs.firebase.database)
    implementation(libs.firebase.database.ktx)
//    debugImplementation(libs.leakcanary.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
}