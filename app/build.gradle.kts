import org.jetbrains.kotlin.kapt3.base.Kapt.kapt


@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "ir.meros.ssistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "ir.meros.ssistant"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)


    // ViewModel Compose
    implementation (libs.lifecycle.viewmodel.compose)
    implementation (libs.androidx.navigation.navigation.compose)
    implementation (libs.androidx.navigation.navigation.compose2)

    //Dagger - Hilt
//    implementation (libs.androidx.hilt.lifecycle.viewmodel)
    kapt(libs.androidx.hilt.hilt.compiler)
    implementation (libs.hilt.navigation.compose)
    implementation (libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)

    // Retrofit
    implementation (libs.retrofit2.retrofit)
    implementation (libs.okhttp3.okhttp)
    implementation(libs.converter.gson)


    // Room
    implementation (libs.androidx.room.room.runtime7)
    implementation (libs.core.ktx)
    implementation(libs.room.ktx)
    ksp(libs.androidx.room.compiler)

    // navigation Animation
    implementation (libs.accompanist.navigation.animation)

    //lotti animation
    implementation (libs.airbnb.lottie.compose)

    // coil fo image processing
    implementation(libs.coil.compose)

    // line chart
    implementation(libs.ycharts)

    // date
    implementation ("com.github.samanzamani:PersianDate:1.7.1")

    // csv reader
    implementation ("com.opencsv:opencsv:5.7.1")


}