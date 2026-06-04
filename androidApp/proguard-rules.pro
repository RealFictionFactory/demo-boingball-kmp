# kotlinx.serialization — keep @Serializable classes and generated serializers
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class **$$serializer { *; }
-dontnote kotlinx.serialization.AnnotationsKt

# Koin — keep ViewModel constructors (viewModelOf uses reflection)
-keepclasseswithmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Kotlin metadata (needed for reflection-based DI)
-keep class kotlin.Metadata { *; }
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod

# Preserve source info for crash reports
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
