package com.dmi.util.android.system

import android.app.Activity
import android.provider.Settings
import android.view.View
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

var Activity.isImmersiveVisibility: Boolean
    get() = unsupported()
    set(value) {
        println("GGG ${window.decorView.systemUiVisibility}")
        if (value) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }