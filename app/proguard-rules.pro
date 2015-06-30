-dontobfuscate

-dontwarn java8.**
-dontwarn org.springframework.**
-dontwarn java.lang.invoke.**
-dontwarn sun.misc.Unsafe
-dontwarn **$$Lambda$**

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