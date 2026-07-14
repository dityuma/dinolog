# DinoLog ProGuard Rules

# Keep Room entities
-keep class com.dites.dinolog.data.local.entity.** { *; }

# Keep Gson serialization
-keep class com.dites.dinolog.data.repository.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

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
