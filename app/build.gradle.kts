plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.gms)
    alias(libs.plugins.hilt)
    id ("kotlin-parcelize")
  //  id("com.google.gms.google-services")

}

android {
    namespace = "com.app.adhyatmah"
    compileSdk = 35
    buildFeatures {
        dataBinding = true
    }
    defaultConfig {
        applicationId = "com.app.adhyatmah"
        minSdk = 24
        targetSdk = 35
        versionCode = 18
        versionName = "1.18"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resConfigs("en", "hi", "mr", "bn", "gu", "or", "ta", "te", "kn", "ml")
    }
    bundle {
        language {
            enableSplit = false  // Disable language splitting
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/gradle/incremental.annotation.processors"
            )
        }
    }

    // ... rest of your android config
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.gson)
    //implementation ("com.google.firebase:firebase-crashlytics-ktx:18.6.1")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")


    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    implementation ("androidx.media3:media3-exoplayer:1.7.1")
    implementation ("androidx.media3:media3-ui:1.2.1")

    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    implementation ("com.tbuonomo:dotsindicator:4.3")

    //country picker
    implementation ("com.hbb20:ccp:2.7.3")


    //otp
    implementation ("com.github.appsfeature:otp-view:1.1")

    // Hilt main library
   /* implementation("com.google.dagger:hilt-android:2.48")
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
*/
    implementation(libs.hilt.android)     // This uses version 2.50 from libs
    implementation(libs.hilt.compiler)

    implementation ("com.github.appsfeature:otp-view:1.1")

    implementation (libs.shimmer)
    implementation (libs.circleimageview)


    // payStack
   // implementation ("com.paystack.android:paystack-ui:0.0.9")
    implementation("com.razorpay:checkout:1.6.41")
   // implementation ("com.razorpay:razorpay-android:1.6.10")

    implementation ("co.paystack.android:paystack:3.1.3")

    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:10.0.0")
    implementation("com.google.firebase:firebase-auth:21.0.1")
// Example
    implementation("com.google.firebase:firebase-firestore:24.0.1")
// Example

    implementation("com.google.android.gms:play-services-location:21.0.1")

}
