package com.dmi.util.scope

import com.dmi.util.debug.IsDebug
import com.dmi.util.log.getStackTraceString

// todo now many wrappers implement own isDisposed. move isDisposed here
interface Disposable {
    /**
     * dispose should't be called twice, throw exception if called
     */
    fun dispose()
}

fun Disposable.debugIfEnabled() = if (IsDebug) debug() else this

private fun Disposable.debug() = object : Disposable {
    private var isDisposed = false
    private val throwable = Throwable()

    override fun dispose() {
        check(!isDisposed)
        this@debug.dispose()
        isDisposed = true
    }

    @Suppress("ProtectedInFinal", "unused")
    protected fun finalize() {
        if (!isDisposed) {
            val stacktrace = getStackTraceString(throwable)
            System.err.println("Object isn't disposed:\n$stacktrace")
        }
    }
}

class Disposables : Disposable {
    private val list = ArrayList<Disposable>()
    private var isDisposed = false

    operator fun plusAssign(disposable: Disposable) {
        check(!isDisposed)
        list.add(disposable)
    }

    override fun dispose() {
        check(!isDisposed)
        list.asReversed().forEach(Disposable::dispose)
        isDisposed = true
    }
}

infix fun Disposable.and(other: Disposable) = object : Disposable {
    override fun dispose() {
        other.dispose()
        this@and.dispose()
    }
}

fun <T : Disposable> T.use(action: (T) -> Unit) {
    try {
        action(this)
    } finally {
        dispose()
    }
}