package com.dmi.util

import com.dmi.util.debug.RefWatcher
import com.dmi.util.log.Log
import rx.Scheduler

private var _mainScheduler: Scheduler? = null
private var _log: Log? = null
private var _refWatcher: RefWatcher? = null

val log: Log get() = _log!!
val mainScheduler: Scheduler get() = _mainScheduler!!
val refWatcher: RefWatcher get() = _refWatcher!!

/**
 * Должно быть вызвано при старте приложения
 */
fun initPlatform(
        log: Log,
        mainScheduler: Scheduler,
        refWatcher: RefWatcher
) {
    _log = log
    _mainScheduler = mainScheduler
    _refWatcher = refWatcher
}