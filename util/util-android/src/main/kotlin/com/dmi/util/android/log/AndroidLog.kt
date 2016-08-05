package com.dmi.util.android.log

import com.dmi.util.log.Log
import com.dmi.util.log.logFullMessage
import com.dmi.util.log.logTag
import com.google.common.base.Splitter
import android.util.Log as AndroidNativeLog

private val ANDROID_MAX_MESSAGE_LENGTH = 4000

object AndroidLog : Log {
    override fun v(message: String) = logSplit(null, message) { AndroidNativeLog.v(logTag(), it) }
    override fun v(t: Throwable, message: String) = logSplit(t, message) { AndroidNativeLog.v(logTag(), it) }
    override fun d(message: String) = logSplit(null, message) { AndroidNativeLog.d(logTag(), it) }
    override fun d(t: Throwable, message: String) = logSplit(t, message) { AndroidNativeLog.d(logTag(), it) }
    override fun i(message: String) = logSplit(null, message) { AndroidNativeLog.i(logTag(), it) }
    override fun i(t: Throwable, message: String) = logSplit(t, message) { AndroidNativeLog.i(logTag(), it) }
    override fun w(message: String) = logSplit(null, message) { AndroidNativeLog.w(logTag(), it) }
    override fun w(t: Throwable, message: String) = logSplit(t, message) { AndroidNativeLog.w(logTag(), it) }
    override fun e(message: String) = logSplit(null, message) { AndroidNativeLog.e(logTag(), it) }
    override fun e(t: Throwable, message: String) = logSplit(t, message) { AndroidNativeLog.e(logTag(), it) }
    override fun wtf(message: String) = logSplit(null, message) { AndroidNativeLog.wtf(logTag(), it) }
    override fun wtf(t: Throwable, message: String) = logSplit(t, message) { AndroidNativeLog.wtf(logTag(), it) }
}

private inline fun logSplit(t: Throwable?, message: String, action: (messagePart: String) -> Unit) {
    val fullMessage = logFullMessage(t, message)
    val splitMessages = Splitter.fixedLength(ANDROID_MAX_MESSAGE_LENGTH).split(fullMessage)
    splitMessages.forEach(action)
}