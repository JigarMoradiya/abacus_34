plugins {
    id("kotlin-kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

    // jetpack
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.jigar.me"
    compileSdk = 36
    ndkVersion = "29.0.13599879"

    defaultConfig {
        applicationId = "com.jigar.me"
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
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )

            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        viewBinding = true
        buildConfig = true
        dataBinding = true
        compose = true
    }
    flavorDimensions += listOf("variant1")
    productFlavors {
        create("dev1") {
            buildConfigField("String","USERS_MODULE", properties["users_module"].toString())
            buildConfigField("String","NEW_MODULE", properties["new_module"].toString())
            buildConfigField("String","LOCATION_MODULE", properties["location_module"].toString())
            buildConfigField("String","EXAM_MODULE", properties["exam_module"].toString())
            resValue("string","app_name", "Abacus Child Leaning App")

            dimension = "variant1"
            applicationId = "com.abacus.puzzle"
            versionCode = 151
            versionName = "15.0.0"
        }
    }
    externalNativeBuild {
        cmake { 
            path("cpp/CMakeLists.txt")
            version = "4.0.2"
        }
    }

    assetPacks += listOf(":asset_install_time")

    packaging {
        resources {
            pickFirsts += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.6")
    implementation("com.google.android.play:asset-delivery-ktx:2.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.7.0")

    // install referrer
    implementation("com.android.installreferrer:installreferrer:2.2")

    // Circular Progress Drawable
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // dagger hilt
    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-android-compiler:2.57.2")
    kapt("androidx.hilt:hilt-compiler:1.3.0")

    //  Manager
    implementation("androidx.work:work-runtime-ktx:2.11.0")
    implementation("androidx.hilt:hilt-work:1.3.0")

    // firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics-ndk")
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-config-ktx:22.1.2")
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.2")
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    implementation("com.google.firebase:firebase-core:21.1.1")

    // google
    implementation("com.google.android.gms:play-services-auth:21.4.0")

    // push notification
    implementation("com.onesignal:OneSignal:5.4.0")

    // api
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("com.squareup.retrofit2:adapter-rxjava2:3.0.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.0")
    implementation("com.jakewharton.rxbinding3:rxbinding-material:3.1.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:5.0.5")
    annotationProcessor("com.github.bumptech.glide:compiler:5.0.5")

    // coroutine
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")

    // life components
//    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.9.4")

    // database
    implementation("androidx.room:room-ktx:2.8.3")
    implementation("androidx.room:room-runtime:2.8.3")
    kapt("androidx.room:room-compiler:2.8.3")

    implementation("net.zetetic:sqlcipher-android:4.11.0@aar")
    implementation("androidx.sqlite:sqlite:2.6.1")

    implementation("androidx.sqlite:sqlite-framework:2.6.1")

    // In App Purchase
    implementation("com.android.billingclient:billing-ktx:8.1.0")

    // gson data
    implementation("com.google.code.gson:gson:2.13.2")

    // Calculator
    implementation("com.fathzer:javaluator:3.0.6")

    // Recyceler view pager indicator
    implementation("ru.tinkoff.scrollingpagerindicator:scrollingpagerindicator:1.2.5")

    // view pager anim
    implementation("com.eftimoff:android-viewpager-transformers:1.0.1@aar")

    implementation("org.apache.commons:commons-text:1.14.0")

    // seekbar
    implementation("com.github.MohammedAlaaMorsi:RangeSeekBar:1.0.6")

    // Country code picker
    implementation("com.hbb20:ccp:2.7.3")

    // rating bar
    implementation("com.github.ome450901:SimpleRatingBar:1.5.1")

    // event bus broadcaster
    implementation("org.greenrobot:eventbus:3.3.1")

    implementation("com.github.dhaval2404:imagepicker:2.1")

    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")

    // jetpack compose
    // Compose BOM ensures all versions stay in sync
    implementation(platform("androidx.compose:compose-bom:2025.11.00"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation")
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

// Icons
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

// Debug tooling
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Ensure new parcelize runtime is used
    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:2.2.21")

    // Globally remove old runtime causing duplicate classes
    configurations.all {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
    }

}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}