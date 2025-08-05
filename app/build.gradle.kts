plugins {
    id("kotlin-kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")

    id("androidx.navigation.safeargs")

    id("com.google.dagger.hilt.android")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.jigar.me"
    compileSdk = 35
    ndkVersion = "25.1.8937393"

    defaultConfig {
        applicationId = "com.jigar.me"
        minSdk = 24
        targetSdk = 35
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
//        freeCompilerArgs += listOf("-Xuse-k2")
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
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
            versionCode = 138
            versionName = "13.0.8"
        }
    }
    externalNativeBuild {
        cmake {
            path("cpp/CMakeLists.txt")
            version = "4.0.2"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // install referrer
    implementation("com.android.installreferrer:installreferrer:2.2")

    // Circular Progress Drawable
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // dagger hilt
    implementation("com.google.dagger:hilt-android:2.56.2")
    kapt("com.google.dagger:hilt-android-compiler:2.56.2")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    //  Manager
    implementation("androidx.work:work-runtime-ktx:2.10.2")
    implementation("androidx.hilt:hilt-work:1.2.0")

    // firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics-ndk")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-config-ktx:22.1.2")
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.2")
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    implementation("com.google.firebase:firebase-core:21.1.1")

    // google
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // push notification
    implementation("com.onesignal:OneSignal:5.1.35")

    // api
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("com.squareup.retrofit2:adapter-rxjava2:3.0.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")
    implementation("com.jakewharton.rxbinding3:rxbinding-material:3.1.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // coroutine
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")

    // life components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    kapt("androidx.lifecycle:lifecycle-compiler:2.9.1")

    // database
    implementation("androidx.room:room-ktx:2.7.2")
    implementation("androidx.room:room-runtime:2.7.2")
    kapt("androidx.room:room-compiler:2.7.2")
    implementation("android.arch.persistence.room:rxjava2:1.1.1")
    implementation("net.zetetic:android-database-sqlcipher:4.5.4@aar")
    implementation("androidx.sqlite:sqlite-ktx:2.5.2")
    implementation("androidx.sqlite:sqlite-framework:2.5.2")

    // In App Purchase
    implementation("com.android.billingclient:billing-ktx:7.1.1")

    // gson data
    implementation("com.google.code.gson:gson:2.11.0")

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

    // rating bar
    implementation("com.github.ome450901:SimpleRatingBar:1.5.0")

    // event bus broadcaster
    implementation("org.greenrobot:eventbus:3.3.1")

    implementation("com.github.dhaval2404:imagepicker:2.1")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}