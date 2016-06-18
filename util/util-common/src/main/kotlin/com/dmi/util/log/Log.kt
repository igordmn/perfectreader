package com.dmi.util.log

interface Log {
    fun v(message: String)
    fun v(t: Throwable, message: String)
    fun d(message: String)
    fun d(t: Throwable, message: String)
    fun i(message: String)
    fun i(t: Throwable, message: String)
    fun w(message: String)
    fun w(t: Throwable, message: String)
    fun e(message: String)
    fun e(t: Throwable, message: String)
    fun wtf(message: String)
    fun wtf(t: Throwable, message: String)
}