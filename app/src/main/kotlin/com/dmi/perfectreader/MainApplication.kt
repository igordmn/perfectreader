package com.dmi.perfectreader

import android.app.Application
import android.content.Context
import android.os.StrictMode
import com.dmi.util.android.log.AndroidLog
import com.dmi.util.coroutine.initThreadContext
import com.dmi.util.debug.IsDebug
import com.dmi.util.log.DebugLog
import com.dmi.util.log.ReleaseLog
import com.github.moduth.blockcanary.BlockCanary
import com.github.moduth.blockcanary.BlockCanaryContext
import kotlinx.coroutines.Dispatchers
import java.io.File


@Suppress("ConstantConditionIf")
class MainApplication : Application() {
    lateinit var context: MainContext
        private set

    private var initAfterPermissions = false

    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("utilAndroid")
        IsDebug = BuildConfig.DEBUG
        val log = initLog()
//        TinyDancer.create().show(this)
        initStrictMode()
        initThreadContext(Dispatchers.Main)
        context = MainContext(log, this)
    }

    fun initAfterPermissions() {
        if (!initAfterPermissions) {
            initBlockCanary()
            initAfterPermissions = true
        }
    }

    private fun initBlockCanary() {
        if (BuildConfig.DEBUG) {
            val path = "$cacheDir/blockcanary/"
            File(path).delete()
            val context = object : BlockCanaryContext() {
                override fun provideBlockThreshold() = 200
                override fun providePath() = path
                override fun displayNotification() = false
            }
            BlockCanary.install(this, context).start()
        }
    }

    private fun initLog() = if (BuildConfig.DEBUG) {
        DebugLog(AndroidLog)
    } else {
        ReleaseLog(AndroidLog)
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

val Context.main: MainContext get() = (applicationContext as MainApplication).context