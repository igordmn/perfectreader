package com.dmi.util.log

import java.io.PrintWriter
import java.io.StringWriter
import java.util.regex.Pattern
import android.util.Log as AndroidNativeLog

class AndroidLog : Log {
    private val STACK_TRACE_INDEX = 5
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

    override fun v(message: String) {
        AndroidNativeLog.v(tag(), fullMessage(null, message))
    }

    override fun v(t: Throwable, message: String) {
        AndroidNativeLog.v(tag(), fullMessage(t, message))
    }

    override fun d(message: String) {
        AndroidNativeLog.d(tag(), fullMessage(null, message))
    }

    override fun d(t: Throwable, message: String) {
        AndroidNativeLog.d(tag(), fullMessage(t, message))
    }

    override fun i(message: String) {
        AndroidNativeLog.i(tag(), fullMessage(null, message))
    }

    override fun i(t: Throwable, message: String) {
        AndroidNativeLog.i(tag(), fullMessage(t, message))
    }

    override fun w(message: String) {
        AndroidNativeLog.w(tag(), fullMessage(null, message))
    }

    override fun w(t: Throwable, message: String) {
        AndroidNativeLog.w(tag(), fullMessage(t, message))
    }

    override fun e(message: String) {
        AndroidNativeLog.e(tag(), fullMessage(null, message))
    }

    override fun e(t: Throwable, message: String) {
        AndroidNativeLog.e(tag(), fullMessage(t, message))
    }

    override fun wtf(message: String) {
        AndroidNativeLog.wtf(tag(), fullMessage(null, message))
    }

    override fun wtf(t: Throwable, message: String) {
        AndroidNativeLog.wtf(tag(), fullMessage(t, message))
    }

    private fun tag(): String {
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
        return tag.substring(tag.lastIndexOf('.') + 1)
    }

    private fun fullMessage(t: Throwable?, message: String) =
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
}