package com.dmi.util.scope

import com.dmi.util.collection.LinkedCache
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

interface Event {
    /**
     * action will be called on even emit
     * action will not be called right in this method "subscribe"
     */
    fun subscribe(action: () -> Unit): Disposable

    fun subscribeOnce(action: () -> Unit): Disposable {
        var subscription: Disposable? = null
        subscription = subscribe {
            action()
            subscription!!.dispose()
        }
        return subscription
    }

    suspend fun wait() {
        suspendCancellableCoroutine<Unit> { cont ->
            val disposable = subscribeOnce {
                cont.resume(Unit)
            }
            cont.invokeOnCancellation {
                disposable.dispose()
            }
        }
    }
}

class EmittableEvent : Event {
    private val listeners = LinkedCache<() -> Unit>()
    private val tempListeners = ArrayList<() -> Unit>()

    fun emit() {
        require(tempListeners.size == 0)
        tempListeners.addAll(listeners)
        try {
            tempListeners.forEach { it() }
        } finally {
            tempListeners.clear()
        }
    }

    override fun subscribe(action: () -> Unit): Disposable {
        var disposed = false
        val entry = listeners.add {
            if (!disposed) action() // may be disposed during iterate tempListeners
        }
        return object : Disposable {
            override fun dispose() {
                entry.remove()
                disposed = true
            }
        }
    }
}