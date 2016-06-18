package com.dmi.util.log

class ReleaseLog(platformLog: Log) : Log by platformLog {
    override fun v(message: String) = Unit
    override fun v(t: Throwable, message: String) = Unit
    override fun d(message: String) = Unit
    override fun d(t: Throwable, message: String) = Unit
}