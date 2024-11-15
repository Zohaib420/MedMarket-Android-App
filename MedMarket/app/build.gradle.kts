plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.hash.medmarket"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hash.medmarket"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.android.volley:volley:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    // Responsive Design
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")

    //Timber for logs
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Styleable Toast
    implementation("io.github.muddz:styleabletoast:2.4.0")

    // Rounded Image View
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Permission Handler
    implementation("com.karumi:dexter:6.2.3")

    // coroutines support for firebase operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    //firebase
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-database-ktx:20.3.1")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))

    implementation("com.google.code.gson:gson:2.8.7")

    // Image cropper
    //noinspection GradleDependency
    implementation("com.github.CanHub:Android-Image-Cropper:4.2.1")

    implementation("io.coil-kt:coil:2.6.0")

    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    //zoom
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
    implementation ("com.squareup.picasso:picasso:2.71828")


}