package com.dmi.util.android.system

import android.provider.Settings
import android.view.WindowManager
import com.dmi.util.android.view.ActivityExt
import com.dmi.util.lang.unsupported
import com.dmi.util.system.Nanos
import com.dmi.util.system.toMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var ActivityExt<*>.screenTimeout: Nanos
    get() = unsupported()
    set(value) {
        var job: Job? = null
        if (value < 0) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            onUserInteraction {
                job?.cancel()
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                job = launch {
                    val systemScreenTimeout = Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 0)
                    delay(value.toMillis().toLong() - systemScreenTimeout)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }