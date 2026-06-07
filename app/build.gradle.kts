plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.quizapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.quizapp"
        minSdk = 23
        targetSdk = 34

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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    
    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    
    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    
    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    
    // Retrofit & OkHttp
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    
    // Glide
    implementation(libs.glide.core)
    annotationProcessor(libs.glide.compiler)
    
    // Security
    implementation(libs.security.crypto)
    
    // WorkManager
    implementation(libs.work.runtime)

    // SwipeRefreshLayout
    implementation(libs.swiperefreshlayout)

    // Google Auth
    implementation(libs.play.services.auth)

    // SQLCipher for encrypted Room DB
    implementation(libs.sqlcipher)
    implementation(libs.sqlite)

    testImplementation(libs.junit)


    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
