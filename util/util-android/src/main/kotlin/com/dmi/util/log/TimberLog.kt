package com.dmi.util.log

import timber.log.Timber

class TimberLog : Log {
    init {
        Timber.plant(Timber.DebugTree())
    }

    override fun v(message: String) = Timber.v(message)
    override fun v(t: Throwable, message: String) = Timber.v(t, message)
    override fun d(message: String) = Timber.d(message)
    override fun d(t: Throwable, message: String) = Timber.d(t, message)
    override fun i(message: String) = Timber.i(message)
    override fun i(t: Throwable, message: String) = Timber.i(t, message)
    override fun w(message: String) = Timber.w(message)
    override fun w(t: Throwable, message: String) = Timber.w(t, message)
    override fun e(message: String) = Timber.e(message)
    override fun e(t: Throwable, message: String) = Timber.e(t, message)
    override fun wtf(message: String) = Timber.wtf(message)
    override fun wtf(t: Throwable, message: String) = Timber.wtf(t, message)
}
