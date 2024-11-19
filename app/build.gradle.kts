plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.proyectolinea_3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.proyectolinea_3"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.ktx)

    // Accompanist (permissions)
    implementation(libs.accompanist.permissions) // Solo una vez, elimina duplicados

    // CameraX
    // Usa una única versión para CameraX y asegúrate de que estén descomentadas
    implementation(libs.androidx.camera.camera2.v110)           // Cámara básica
    implementation(libs.androidx.camera.lifecycle.v110)         // Integración con el ciclo de vida
    implementation(libs.androidx.camera.view.v100alpha31)       // Vista de la cámara

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.runtime.livedata)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.5.1")) // Firebase BOM para unificar versiones
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.androidx.ui.test.android)
    implementation(libs.firebase.storage.ktx)


    //Google Maps
//    implementation("com.google.maps.android:maps-compose:4.4.1")
//    implementation(libs.play.services.maps.v1802)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("com.google.accompanist:accompanist-permissions:0.30.1")
    implementation ("com.google.maps.android:maps-compose:6.2.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
//    implementation(libs.androidx.appcompat)





    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Coil
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation(libs.glide)




}
