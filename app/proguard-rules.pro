-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Uncomment to hide original source file name in stack traces
#-renamesourcefileattribute SourceFile

# Strip logs in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
}

##---------------Retrofit (com.squareup.retrofit2:retrofit:3.0.0, package stays retrofit2.*)--------##
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}

##---------------OkHttp3--------##
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

##---------------RxJava 2--------##
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

##---------------Gson--------##
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

##---------------Glide--------##
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

##---------------Room--------##
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

##---------------EventBus--------##
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

##---------------SQLCipher--------##
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

##---------------Kotlin Coroutines--------##
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

##---------------Kotlin Parcelize--------##
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable

##---------------Navigation--------##
-keep class androidx.navigation.fragment.NavHostFragment
-keep class * extends androidx.fragment.app.Fragment {}

##---------------ViewBinding--------##
-keep class **_ViewBinding { *; }

##---------------BouncyCastle--------##
-keep class org.bouncycastle.** { *; }
-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.** { *; }
-dontwarn org.bouncycastle.**

##---------------Material--------##
-keep class com.google.android.material.textfield.TextInputLayout { *; }
-keep class com.google.android.material.internal.CollapsingTextHelper { *; }

##---------------App model classes--------##
# Everything under data.model.** is already covered by the blanket rule below,
# including data.model.dbtable.abacus_all_data.** (Level/Abacus/Pages/Category/
# Set/SetProgress). The old dbtable.exam/inapp/suduko lines matched packages
# that no longer exist in the codebase, so they were removed as dead config.
-keepclassmembers class com.jigar.me.data.model.** { *; }
-keepclassmembers class com.jigar.me.data.model.data.** { *; }
-keepclassmembers class com.jigar.me.data.model.pages.** { *; }
-keepclassmembers class com.jigar.me.data.model.dbtable.abacus_all_data.** { *; }

##---------------Gson-persisted game state (SharedPreferences/SavedStateHandle)--------##
# These aren't under data.model.** and have no @SerializedName, so R8 field
# renaming breaks Gson round-trips across separate release builds (mapping
# isn't stable build-to-build) -- e.g. saved sudoku progress silently
# disappearing after an app update.
-keepclassmembers class com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SavedSudokuGame { *; }
-keepclassmembers class com.jigar.me.ui.view.home.screens.math_game_zone.sudoku.play.viewmodel.SudokuPuzzle { *; }
-keepclassmembers class com.jigar.me.ui.view.home.screens.math_game_zone.target_number.components.TargetUiState { *; }

##---------------Suppress irrelevant warnings--------##
-dontwarn sun.misc.Unsafe
-dontwarn javax.xml.bind.DatatypeConverter
-dontwarn com.fasterxml.jackson.**
-dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite
