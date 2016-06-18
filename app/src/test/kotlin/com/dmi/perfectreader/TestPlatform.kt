package com.dmi.perfectreader

import com.dmi.util.debug.DisabledRefWatcher
import com.dmi.util.initPlatform
import com.dmi.util.log.ConsoleLog
import rx.schedulers.Schedulers
import java.util.concurrent.Executors

fun initTestPlatform() {
    val mainScheduler = Schedulers.from(Executors.newSingleThreadExecutor())
    initPlatform(ConsoleLog, mainScheduler, DisabledRefWatcher)
}