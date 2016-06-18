package com.dmi.util.log

import java.io.PrintWriter
import java.io.StringWriter
import java.util.regex.Pattern

private val STACK_TRACE_INDEX = 5
private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

fun logTag(): String {
    val stackTrace = java.lang.Throwable().stackTrace
    if (stackTrace.size <= STACK_TRACE_INDEX)
        throw IllegalStateException("Synthetic stacktrace don't have enough elements. Maybe it is because of proguard")
    return stackTraceTag(stackTrace[STACK_TRACE_INDEX])
}

private fun stackTraceTag(element: StackTraceElement): String {
    var tag = element.className
    val m = ANONYMOUS_CLASS.matcher(tag)
    if (m.find())
        tag = m.replaceAll("")
    return tag.substringAfterLast('.').substringBefore('$')
}

fun logFullMessage(t: Throwable?, message: String) =
        if (t != null) {
            message + "\n" + getStackTraceString(t)
        } else {
            message
        }

// Don't replace by Log.getStackTraceString(). It hides UnknownHostException
private fun getStackTraceString(t: Throwable): String {
    val sw = StringWriter(256)
    val pw = PrintWriter(sw, false)
    t.printStackTrace(pw)
    pw.flush()
    return sw.toString()
}