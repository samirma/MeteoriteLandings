# R8 runs in full mode (AGP default). These are the app's own keep rules; Retrofit, OkHttp,
# Room, Hilt and kotlinx.serialization ship their own consumer rules.

# --- Crash reports -----------------------------------------------------------------------------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Strip debug logging from release builds ----------------------------------------------------
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# --- kotlinx.serialization ----------------------------------------------------------------------
# The plugin generates a `Companion.serializer()` per @Serializable type and R8's full mode can
# strip the companion when it is only reached reflectively from `serializer<T>()`.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# The DTOs themselves: field names are the JSON contract for properties without @SerialName.
-keep,allowobfuscation,allowshrinking class com.antonio.samir.meteoritelandingsspots.data.remote.model.** { *; }

# --- Room ---------------------------------------------------------------------------------------
# Column names are derived from field names for properties without @ColumnInfo.
-keep class com.antonio.samir.meteoritelandingsspots.data.local.model.** { *; }

# --- Kotlin -------------------------------------------------------------------------------------
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
