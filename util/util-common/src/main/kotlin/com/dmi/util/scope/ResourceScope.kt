package com.dmi.util.scope

class ResourceScope {
    private val resources = ArrayList<Disposable>()

    fun <T: Disposable> T.use(): T {
        resources.add(this)
        return this
    }

    fun disposeAll() {
        resources.asReversed().forEach(Disposable::dispose)
    }
}

inline fun <T> resourceScope(consume: ResourceScope.() -> T): T {
    val scope = ResourceScope()
    try {
        return scope.consume()
    } finally {
        scope.disposeAll()
    }
}