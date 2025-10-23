plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "com.example.datasources.database"
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

    room {
        schemaDirectory("$projectDir/schemas")
    }
}


dependencies {

    implementation(project(":datasources:database:dao"))

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    implementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.junit.runner)
    testImplementation(libs.io.mockk)
    androidTestImplementation(libs.io.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation(libs.robolectric)

    implementation(libs.io.insert.koin)
}