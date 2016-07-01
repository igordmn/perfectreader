package com.dmi.util.android.log

import com.dmi.util.lang.returnUnit
import com.dmi.util.log.Log
import com.dmi.util.log.logFullMessage
import com.dmi.util.log.logTag
import android.util.Log as AndroidNativeLog

object AndroidLog : Log {
    override fun v(message: String) = AndroidNativeLog.v(logTag(), logFullMessage(null, message)).returnUnit()
    override fun v(t: Throwable, message: String) = AndroidNativeLog.v(logTag(), logFullMessage(t, message)).returnUnit()
    override fun d(message: String) = AndroidNativeLog.d(logTag(), logFullMessage(null, message)).returnUnit()
    override fun d(t: Throwable, message: String) = AndroidNativeLog.d(logTag(), logFullMessage(t, message)).returnUnit()
    override fun i(message: String) = AndroidNativeLog.i(logTag(), logFullMessage(null, message)).returnUnit()
    override fun i(t: Throwable, message: String) = AndroidNativeLog.i(logTag(), logFullMessage(t, message)).returnUnit()
    override fun w(message: String) = AndroidNativeLog.w(logTag(), logFullMessage(null, message)).returnUnit()
    override fun w(t: Throwable, message: String) = AndroidNativeLog.w(logTag(), logFullMessage(t, message)).returnUnit()
    override fun e(message: String) = AndroidNativeLog.e(logTag(), logFullMessage(null, message)).returnUnit()
    override fun e(t: Throwable, message: String) = AndroidNativeLog.e(logTag(), logFullMessage(t, message)).returnUnit()
    override fun wtf(message: String) = AndroidNativeLog.wtf(logTag(), logFullMessage(null, message)).returnUnit()
    override fun wtf(t: Throwable, message: String) = AndroidNativeLog.wtf(logTag(), logFullMessage(t, message)).returnUnit()
}