package com.dmi.util.android.jni

@Retention(AnnotationRetention.BINARY)
@Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.FILE,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.FIELD
)
annotation class UsedByNative