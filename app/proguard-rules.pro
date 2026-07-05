# Proguard rules for T1 V2

# 1. Kotlinx Serialization keep rules
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep serializable classes companion objects
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keep class * implements kotlinx.serialization.KSerializer {
    *;
}

# Keep local database entities, domain models, and network DTOs
-keep class com.example.t1.data.remote.model.** { *; }
-keep class com.example.t1.data.database.entity.** { *; }
-keep class com.example.t1.domain.model.** { *; }

# 2. Room database keep rules
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.RoomDatabase$Callback
-keep class * implements androidx.room.migration.Migration
-dontwarn androidx.room.**

# 3. Supabase and Ktor/Http client keep rules
-dontwarn io.github.jan.supabase.**
-dontwarn io.ktor.**
-dontwarn io.netty.**
-dontwarn org.apache.http.**
-dontwarn org.slf4j.**
-dontwarn okhttp3.**

# 4. Hilt/Dagger DI keep rules
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponentBuilder { *; }
-keep class * extends android.app.Application { *; }
-dontwarn com.example.t1.MainApplication_GeneratedInjector
