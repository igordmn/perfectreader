package com.dmi.util.android.jni

@UsedByNative
fun currentStackTrace(): String {
    val s = StringBuilder()
    val stackTrace = Thread.currentThread().stackTrace
    for (i in 3 until stackTrace.size) {
        if (s.isNotEmpty())
            s.append('\n')
        s.append("\tat ")
        s.append(stackTrace[i].toString())
    }
    return s.toString()
}