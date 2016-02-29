-dontobfuscate

# Kotlin
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}


# Anko
-dontwarn org.jetbrains.anko.**


# Guava

-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
-dontwarn org.springframework.**
-dontwarn sun.misc.Unsafe


# SimpleFragment

-dontwarn me.tatarka.simplefragment.SimpleFragment$**


# Readium

-keep class org.readium.sdk.android.**
-keepclassmembers class org.readium.sdk.android.** {
    *;
}


# Dagger

-keep class javax.inject.** { *; }
-keep class dagger.** { *; }
-keep class **$$ModuleAdapter
-keep class **$$InjectAdapter
-keep class **$$StaticInjection

-keepclassmembers class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}


# ButterKnife

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


# Icepick

-dontwarn icepick.**
-keep class **$$Icepick { *; }
-keepnames class * { @icepick.State *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}
