package com.dmi.util.log

import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Math.min
import java.util.regex.Pattern

private val MAX_STACKTRACE_LENGTH = 40000
private val STACK_TRACE_INDEX = 5
private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

fun logTag(): String {
    val stackTrace = java.lang.Throwable().stackTrace
    return if (STACK_TRACE_INDEX < stackTrace.size) {
        stackTraceTag(stackTrace[STACK_TRACE_INDEX])
    } else {
        "unknown"
    }
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
fun getStackTraceString(t: Throwable): String {
    val sw = StringWriter(MAX_STACKTRACE_LENGTH)
    val pw = PrintWriter(sw, false)
    t.printStackTrace(pw)
    pw.flush()
    val fullMsg = sw.toString()
    return fullMsg.substring(0, min(fullMsg.length, MAX_STACKTRACE_LENGTH))
}