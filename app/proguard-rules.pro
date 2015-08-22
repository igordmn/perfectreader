-dontobfuscate

# Guava

-dontwarn java.lang.invoke.**
-dontwarn org.springframework.**
-dontwarn sun.misc.Unsafe


# Retrolambda

-dontwarn java8.**
-dontwarn **$$Lambda$**


# SimpleFragment

-dontwarn me.tatarka.simplefragment.SimpleFragment$**


# Typoweb

-keep class com.dmi.typoweb.**
-keepclassmembers class com.dmi.typoweb.** {
    *;
}

-keep class org.readium.sdk.android.**
-keepclassmembers class org.readium.sdk.android.** {
    *;
}

-keepclassmembers class * {
    @com.dmi.typoweb.JavascriptInterface <methods>;
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
