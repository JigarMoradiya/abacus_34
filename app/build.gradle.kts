import com.android.build.api.variant.BuildConfigField

plugins {
    id("kotlin-kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    id("androidx.navigation.safeargs")

    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.jigar.me"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jigar.me"
        minSdk = 24
        targetSdk = 34
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
    }
    flavorDimensions += listOf("variant1")
    productFlavors {
        create("dev1") {
            buildConfigField("String","USERS_MODULE", properties["users_module"].toString())
            buildConfigField("String","STUDENT_MODULE", properties["student_module"].toString())
            buildConfigField("String","SUBSCRIPTIONS_MODULE", properties["subscriptions_module"].toString())
            buildConfigField("String","LOCATION_MODULE", properties["location_module"].toString())
            buildConfigField("String","EXAM_MODULE", properties["exam_module"].toString())
            buildConfigField("String","ONE_SIGNAL", properties["one_signal_id"].toString())
            buildConfigField("String","ORGANIZER_ID", properties["organizer_id"].toString())
            resValue("string","app_name", "Abacus Child Leaning App")

            dimension = "variant1"
            applicationId = "com.abacus.puzzle"
            versionCode = 2
            versionName = "10.0.1"
        }
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // dagger hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics-ndk")
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-config-ktx:22.0.0")
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.firebase:firebase-core:21.1.1")

    // google
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.android.gms:play-services-ads:23.2.0")

    // push notification
    implementation("com.onesignal:OneSignal:[5.0.0, 5.99.99]")

    // api
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.20")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // coroutine
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.3")

    // life components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    kapt("androidx.lifecycle:lifecycle-compiler:2.8.3")

    // database
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("android.arch.persistence.room:rxjava2:1.1.1")

    // In App Purchase
    implementation("com.android.billingclient:billing-ktx:7.0.0")

    // gson data
    implementation("com.google.code.gson:gson:2.10.1")

    // Calculator
    implementation("com.fathzer:javaluator:3.0.2")

    // Recyceler view pager indicator
    implementation("ru.tinkoff.scrollingpagerindicator:scrollingpagerindicator:1.2.1")

    // view pager anim
    implementation("com.eftimoff:android-viewpager-transformers:1.0.1@aar")

    implementation("org.apache.commons:commons-text:1.9")

    // seekbar
    implementation("com.github.MohammedAlaaMorsi:RangeSeekBar:1.0.6")

    // Country code picker
    implementation("com.hbb20:ccp:2.7.0")

    // otp view
    implementation("com.github.mukeshsolanki.android-otpview-pinview:otpview:3.1.0")

    // Download Manger Library
    implementation("androidx.tonyodev.fetch2:xfetch2:3.1.6")
    implementation("androidx.tonyodev.fetch2okhttp:xfetch2okhttp:3.1.6")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.9.0")

    // rating bar
    implementation("com.github.ome450901:SimpleRatingBar:1.5.0")

    // image viewer pager
    implementation("com.facebook.fresco:fresco:2.4.0")
    implementation("com.github.stfalcon:frescoimageviewer:0.5.0")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}