package com.dmi.util.log

interface Log {
    companion object : Log {
        private lateinit var log: Log

        fun init(log: Log) { this.log = log }

        override fun v(message: String) = log.v(message)
        override fun v(t: Throwable, message: String) = log.v(t, message)
        override fun d(message: String) = log.d(message)
        override fun d(t: Throwable, message: String) = log.d(t, message)
        override fun i(message: String) = log.i(message)
        override fun i(t: Throwable, message: String) = log.i(t, message)
        override fun w(message: String) = log.w(message)
        override fun w(t: Throwable, message: String) = log.w(t, message)
        override fun e(message: String) = log.e(message)
        override fun e(t: Throwable, message: String) = log.e(t, message)
        override fun wtf(message: String) = log.wtf(message)
        override fun wtf(t: Throwable, message: String) = log.wtf(t, message)
    }

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