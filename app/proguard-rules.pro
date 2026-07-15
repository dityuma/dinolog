# DinoLog ProGuard Rules

# Keep Room entities
-keep class com.dites.dinolog.data.local.entity.** { *; }

# Keep all data classes used for Gson serialization
-keep class com.dites.dinolog.data.local.entity.ReptileEntity { *; }
-keep class com.dites.dinolog.data.local.entity.FeedingLogEntity { *; }
-keep class com.dites.dinolog.data.local.entity.BrumasiLogEntity { *; }

# Keep backup data classes (wherever they are defined)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep Gson annotations
-keepattributes *Annotation*
-keepattributes Signature
-keep class com.google.gson.** { *; }

# Keep Coil
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep Vico charts
-keep class com.patrykandpatrick.vico.** { *; }

# Keep DataStore
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep Room DAOs
-keep interface com.dites.dinolog.data.local.dao.** { *; }

# General Android
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
