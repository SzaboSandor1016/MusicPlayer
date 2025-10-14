plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.di"
    compileSdk = 36

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {

        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(project(":features:sync:domain"))
    implementation(project(":features:musicsource:data"))
    implementation(project(":features:musicsource:domain"))
    implementation(project(":features:playlists:data"))
    implementation(project(":features:playlists:domain"))
    implementation(project(":features:playlists:presentation"))
    implementation(project(":features:songs:data"))
    implementation(project(":features:songs:domain"))
    implementation(project(":features:songs:presentation"))
    implementation(project(":features:albums:data"))
    implementation(project(":features:albums:domain"))
    implementation(project(":features:albums:presentation"))
    implementation(project(":features:artists:data"))
    implementation(project(":features:artists:domain"))
    implementation(project(":features:artists:presentation"))
    implementation(project(":features:genres:data"))
    implementation(project(":features:genres:domain"))
    implementation(project(":features:genres:presentation"))

    implementation(project(":datasources:database"))
    implementation(project(":datasources:database:dao"))
    implementation(project(":datasources:mediastore:data"))
    implementation(project(":datasources:mediastore:domain"))

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.io.mockk)
    androidTestImplementation(libs.io.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.io.insert.koin)
}