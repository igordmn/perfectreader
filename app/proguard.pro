-optimizations !class/merging/horizontal
-optimizationpasses 7
-allowaccessmodification
-dontobfuscate

-dontpreverify
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

-dontnote kotlin.**
-dontnote com.google.**
-dontnote com.android.**
-dontnote com.nineoldandroids.**
-dontnote org.junit.**
-dontnote junit.**
-dontnote android.**
-dontnote org.apache.**
-dontnote org.mockito.**
-dontnote org.objenesis.**
-dontnote com.astuetz.**
-dontnote com.mikepenz.**
-dontnote com.rey.**
-dontnote com.pnikosis.**
-dontnote rx.internal.**
-dontnote org.adw.library.**
-dontnote com.github.moduth.**
-dontnote org.jetbrains.annotations.**

# Java
-keepattributes *Annotation*

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Kotlin
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Android
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keepclassmembers public class * extends android.view.View {
     void set*(***);
     *** get*();
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# Anko
-dontwarn org.jetbrains.anko.**

# Guava
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn afu.org.checkerframework.**
-dontwarn org.checkerframework.**
-dontwarn com.google.errorprone.annotations.*
-dontwarn com.google.common.**

# RxJava
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Test
-dontwarn org.mockito.**
-dontwarn android.test.**
-dontwarn org.junit.**

# BlockCanary
-dontwarn com.github.moduth.**

# PerfectReader
-keep @interface com.dmi.util.android.jni.UsedByNative
-keep @com.dmi.util.android.jni.UsedByNative class *
-keepclassmembers class ** {
    @com.dmi.util.android.jni.UsedByNative *;
}

-keep class kotlinx.coroutines.** { *; }
-keep class com.dmi.util.android.jni.** { *; }
-keepclassmembers class com.dmi.util.android.jni.** { *; }

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.dmi.perfectreader.**$$serializer { *; }
-keepclassmembers class com.dmi.perfectreader.** {
    *** Companion;
}
-keepclasseswithmembers class com.dmi.perfectreader.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}