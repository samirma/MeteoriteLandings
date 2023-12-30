import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.antonio.samir.meteoritelandingsspots"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.antonio.samir.meteoritelandingsspots"
        minSdk = 24
        targetSdk = 34
        versionCode = 19
        versionName = "2.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val properties = Properties().apply {
        this.load(file("production.properties").inputStream())
    }

    signingConfigs {
        getByName("debug") {
            val devKey = "dev_key"
            keyAlias = devKey
            keyPassword = devKey
            storeFile = file("dev_key.jks")
            storePassword = devKey
        }
        create("release") {
            val keyPassword = properties.getProperty("keyStorePassword")
            keyAlias = properties.getProperty("keyAlias")
            this.keyPassword = keyPassword
            storeFile = file(properties.getProperty("keyStore"))
            storePassword = keyPassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            versionNameSuffix = "-dev"
            applicationIdSuffix = ".dev"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(project(":designsystem"))

    implementation(libs.core.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.runtime.tracing)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.play.services.location)

    implementation(libs.kpermissions)
    implementation(libs.kpermissions.coroutines)

    //Paging
    implementation(libs.paging.compose)
    implementation(libs.androidx.paging.runtime)

    //Navigation
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)

    //Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)


    implementation(libs.androidx.work.runtime.ktx)

    //Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.navigation.compose)

    //DataStore
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.fixture)
    testImplementation(libs.mockk)
}