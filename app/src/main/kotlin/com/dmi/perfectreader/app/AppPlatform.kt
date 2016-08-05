package com.dmi.perfectreader.app

import android.os.StrictMode
import com.dmi.perfectreader.BuildConfig
import com.dmi.util.android.log.AndroidLog
import com.dmi.util.android.system.ThreadPriority
import com.dmi.util.android.system.setPriority
import com.dmi.util.debug.DisabledRefWatcher
import com.dmi.util.ext.async
import com.dmi.util.initPlatform
import com.dmi.util.log
import com.dmi.util.log.DebugLog
import com.dmi.util.log.ReleaseLog
import com.github.moduth.blockcanary.BlockCanary
import com.github.moduth.blockcanary.BlockCanaryContext
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.Executors.newSingleThreadExecutor
import java.util.concurrent.ThreadFactory

val dataAccessScheduler = singleThreadScheduler("dataAccess", ThreadPriority.BACKGROUND)
val bookLoadScheduler = singleThreadScheduler("bookLoad", ThreadPriority.BACKGROUND)
val glBackgroundScheduler = singleThreadScheduler("pagePaint", ThreadPriority.BACKGROUND)
val pageLoadScheduler = singleThreadScheduler("pageLoad", ThreadPriority.BACKGROUND)

fun dataAccessAsync(run: () -> Unit) = async(dataAccessScheduler, run)

private fun singleThreadScheduler(name: String, priority: ThreadPriority) =
        Schedulers.from(newSingleThreadExecutor(SingleThreadFactory(name, priority)))

private class SingleThreadFactory(
        private val name: String,
        private val priority: ThreadPriority
) : ThreadFactory {
    override fun newThread(runnable: Runnable) = Thread(runnable, name).apply {
        setPriority(this@SingleThreadFactory.priority)
    }
}

fun initAndroidPlatform(app: App) {
    val log = initLog()
    initMainExceptionCatcher()
    val scheduler = AndroidSchedulers.mainThread()
    val refWatcher = initRefWatcher(app)
    initPlatform(log, scheduler, refWatcher)
    initStrictMode()
    initBlockCanary(app)
}

private fun initLog() = if (BuildConfig.DEBUG) {
    DebugLog(AndroidLog)
} else {
    ReleaseLog(AndroidLog)
}

fun initMainExceptionCatcher() {
    val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        log.e(throwable, "Exception in thread \"${thread.name}\":")
        oldHandler.uncaughtException(thread, Throwable("See stacktrace above"))
    }
}

private fun initRefWatcher(app: App) = if (BuildConfig.DEBUG_LEAKCANARY) {
    RefWatcherWrapper(LeakCanary.install(app))
} else {
    DisabledRefWatcher
}

private class RefWatcherWrapper(private val original: RefWatcher) : com.dmi.util.debug.RefWatcher {
    override fun watch(watchedReference: Any) = original.watch(watchedReference)
}

private fun initBlockCanary(app: App) {
    if (BuildConfig.DEBUG_BLOCKCANARY) {
        val context = object : BlockCanaryContext() {
            override fun getConfigBlockThreshold() = BuildConfig.DEBUG_BLOCKCANARY_LIMIT_MILLIS
            override fun isNeedDisplay() = false
            override fun getLogPath() = "/perfectreader-debug/blockcanary"
        }
        BlockCanary.install(app, context).start()
    }
}

private fun initStrictMode() {
    if (BuildConfig.DEBUG_STRICTMODE) {
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