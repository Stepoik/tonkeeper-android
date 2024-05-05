plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = Build.namespacePrefix("wallet.data.tonconnect")
    compileSdk = Build.compileSdkVersion

    defaultConfig {
        minSdk = Build.minSdkVersion
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(Dependence.KotlinX.coroutines)
    implementation(Dependence.Koin.core)

    implementation(project(Dependence.Lib.extensions))

    implementation(project(Dependence.Lib.network))
    implementation(Dependence.Squareup.retrofit)
    implementation(Dependence.Squareup.retrofitMoshi)
    kapt(Dependence.Squareup.moshiCodegen)
    implementation(Dependence.Squareup.moshi)
}