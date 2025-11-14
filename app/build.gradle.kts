plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.conexion"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.conexion"
        minSdk = 24
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
}

dependencies {

    // Firebase BoM (Bill of Materials)
    implementation(platform(libs.firebase.bom))
    // ✅ AÑADIMOS LA DEPENDENCIA DE FIREBASE AUTH
    implementation(libs.firebase.auth)
    // ✅ AÑADIMOS LA DEPENDENCIA DE CLOUD FIRESTORE
    implementation("com.google.firebase:firebase-firestore")

    // ✅ AÑADIMOS LA DEPENDENCIA PARA SWIPE TO REFRESH
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    implementation(libs.jbcrypt)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}