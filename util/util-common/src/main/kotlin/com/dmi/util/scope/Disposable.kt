package com.dmi.util.scope

interface Disposable {
    /**
     * dispose should't be called twice, throw exception if called
     */
    fun dispose()
}

class Disposables : Disposable {
    private val list = ArrayList<Disposable>()
    private var disposed = false

    @Suppress("ProtectedInFinal", "unused")
    protected fun finalize() {
        if (!disposed)
            println("Object $this isn't disposed")
    }

    operator fun plusAssign(disposable: Disposable) {
        check(!disposed)
        list.add(disposable)
    }

    override fun dispose() {
        check(!disposed)
        list.asReversed().forEach(Disposable::dispose)
        disposed = true
    }
}

infix fun Disposable.and(other: Disposable) = object : Disposable {
    override fun dispose() {
        other.dispose()
        this@and.dispose()
    }
}