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

##---------------Retrofit 3 (package changed from retrofit.* to retrofit3.*)--------##
-dontwarn retrofit3.**
-keep class retrofit3.** { *; }
-keepclasseswithmembers class * {
    @retrofit3.http.* <methods>;
}
-keepclassmembernames interface * {
    @retrofit3.http.* <methods>;
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
-keepclassmembers class com.jigar.me.data.model.** { *; }
-keepclassmembers class com.jigar.me.data.model.data.** { *; }
-keepclassmembers class com.jigar.me.data.model.pages.** { *; }
-keepclassmembers class com.jigar.me.data.model.dbtable.exam.** { *; }
-keepclassmembers class com.jigar.me.data.model.dbtable.inapp.** { *; }
-keepclassmembers class com.jigar.me.data.model.dbtable.suduko.** { *; }

##---------------Suppress irrelevant warnings--------##
-dontwarn sun.misc.Unsafe
-dontwarn javax.xml.bind.DatatypeConverter
-dontwarn com.fasterxml.jackson.**
