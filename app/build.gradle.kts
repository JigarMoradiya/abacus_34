import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.compose)
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

    signingConfigs {
        create("release") {
            storeFile = file("/Users/jigarmoradiya/Documents/newProjects/abacus_34/app/keystore_live/abacus.jks")
            storePassword = "android"
            keyAlias = "android"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
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
            buildConfigField("String", "USERS_MODULE", properties["users_module"].toString())
            buildConfigField("String", "NEW_MODULE", properties["new_module"].toString())
            buildConfigField("String", "LOCATION_MODULE", properties["location_module"].toString())
            buildConfigField("String", "EXAM_MODULE", properties["exam_module"].toString())
            resValue("string", "app_name", "Abacus")

            dimension = "variant1"
            applicationId = "com.abacus.puzzle"
            versionCode = 162
            versionName = "15.0.8"
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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.asset.delivery.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)

    // Install referrer
    implementation(libs.install.referrer)

    // Swipe refresh
    implementation(libs.androidx.swiperefreshlayout)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.androidx.hilt.compiler)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.core)

    // Google
    implementation(libs.play.services.auth)

    // Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Push notifications
    implementation(libs.onesignal)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.rxjava2)
    implementation(libs.retrofit.adapter.rxjava2)
    implementation(libs.rxandroid)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.rxbinding.material)

    // Glide
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.common.java8)

    // Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)

    // SQLCipher + SQLite
    implementation(libs.sqlcipher.android)
    implementation(libs.androidx.sqlite)
    implementation(libs.androidx.sqlite.framework)

    // Billing
    implementation(libs.billing.ktx)

    // Data
    implementation(libs.gson)
    implementation(libs.javaluator)

    // UI components
    implementation(libs.scrolling.pager.indicator)
    implementation(libs.viewpager.transformers)
    implementation(libs.commons.text)
    implementation(libs.range.seekbar)
    implementation(libs.ccp)
    implementation(libs.simple.rating.bar)
    implementation(libs.eventbus)
    implementation(libs.imagepicker)

    // Media
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.coil.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Kotlin
    implementation(libs.kotlin.parcelize.runtime)

    // Accompanist
    implementation(libs.accompanist.flowlayout)

    configurations.all {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
    }
}

kapt {
    correctErrorTypes = true
}
