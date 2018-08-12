package com.dmi.util.android.system

import android.view.Choreographer
import com.dmi.util.system.Display
import com.dmi.util.system.Nanos
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.System.nanoTime
import kotlin.coroutines.resume
import kotlin.math.max

object AndroidDisplay : Display {
    private var lastReturnedTime = -1L

    override val currentTime: Nanos
        get(): Nanos {
            lastReturnedTime = nanoTime()
            return lastReturnedTime
        }

    override suspend fun waitVSyncTime(): Nanos {
        val frameTime: Nanos = suspendCancellableCoroutine { cont ->
            val callback = Choreographer.FrameCallback {
                cont.resume(it)
            }
            Choreographer.getInstance().postFrameCallback(callback)
            cont.invokeOnCancellation {
                Choreographer.getInstance().removeFrameCallback(callback)
            }
        }
        /**
         * same as [android.view.animation.AnimationUtils.currentAnimationTimeMillis]
         */
        return max(lastReturnedTime, frameTime)
    }
}