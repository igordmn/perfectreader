package com.dmi.perfectreader

import android.app.Application
import android.content.Context
import android.os.StrictMode
import com.dmi.util.android.log.AndroidLog
import com.dmi.util.coroutine.initThreadContext
import com.dmi.util.lang.NoStackTraceThrowable
import com.dmi.util.log.DebugLog
import com.dmi.util.log.Log
import com.dmi.util.log.ReleaseLog
import kotlinx.coroutines.Dispatchers

@Suppress("ConstantConditionIf")
class MainApplication : Application() {
    lateinit var main: Main
        private set

    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("utilAndroid")
        val log = initLog()
        initMainExceptionCatcher(log)
        initStrictMode()
        initThreadContext(Dispatchers.Main)
//        TinyDancer.create().show(this)
        main = Main(log, this)
    }

    private fun initLog() = if (BuildConfig.DEBUG) {
        DebugLog(AndroidLog)
    } else {
        ReleaseLog(AndroidLog)
    }

    private fun initMainExceptionCatcher(log: Log) {
        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            log.e(throwable, "Exception in thread \"${thread.name}\":")
            oldHandler.uncaughtException(thread, NoStackTraceThrowable("See stacktrace above"))
        }
    }

    private fun initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskWrites()
                    .detectNetwork()
                    .detectCustomSlowCalls()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()
                    .detectLeakedRegistrationObjects()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build())
        }
    }
}

val Context.main: Main get() = (applicationContext as MainApplication).main