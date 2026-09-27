# kotlinx.serialization — keep @Serializable classes and generated serializers
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class **$$serializer { *; }
-dontnote kotlinx.serialization.AnnotationsKt

# Koin does not use reflection here (the Koin compiler plugin wires constructors at build
# time). Kept conservatively; verify a release build on a device before removing.
-keepclasseswithmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Kotlin metadata and attributes; kept conservatively, see the Koin note above.
-keep class kotlin.Metadata { *; }
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod

# Preserve source info for crash reports
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
