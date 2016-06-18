-dontpreverify
-dontobfuscate
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

# Support library
-dontwarn android.support.**

# Anko
-dontwarn org.jetbrains.anko.**

# Guava
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Test
-dontwarn org.mockito.**
-dontwarn android.test.**
-dontwarn org.junit.**

# PerfectReader
-keep class com.dmi.util.opengl.GLSurfaceViewExt$RendererExt

# BlockCanary
-dontwarn com.github.moduth.**