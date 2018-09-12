package com.dmi.util.scope

import com.dmi.util.coroutine.threadContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * For copy all values from one thread to another synchronously
 */
class CopyScope(
        private val copyContext: CoroutineContext,
        thisContext: CoroutineContext = threadContext
) : Disposable {
    private val scope = Scope()
    private var copyScope: Scope? = null

    private val job = Job()
    private val delegates = ArrayList<ComputedDelegate<*>>()

    init {
        launch(copyContext, parent = job) {
            copyScope = Scope()
        }
    }

    override fun dispose() {
        scope.dispose()
        job.cancel()
        launch(copyContext) {
            copyScope?.dispose()
        }
    }

    fun <T> computed(compute: () -> T) = ComputedDelegate(compute)

    inner class ComputedDelegate<T>(compute: () -> T) : ReadOnlyProperty<Any?, T> {
        val original = scope.cached(compute)
        lateinit var copy: Scope.VariableDelegate<T>

        private var changed = false

        @Volatile
        private var writeValue: T = original.value

        @Volatile
        private var readValue: T = original.value

        @Volatile
        private var writeChanged = false

        @Volatile
        private var readChanged = false

        init {
            delegates.add(this)

            launch(copyContext, parent = job) {
                copy = copyScope!!.observable(readValue)
            }

            original.onchange.subscribe {
                changed = true
                write.schedule()
            }
        }

        fun write() {
            if (changed)
                writeValue = original.value
            writeChanged = changed
            changed = false
        }

        fun read() {
            readValue = writeValue
            readChanged = writeChanged
        }

        fun apply() {
            if (readChanged)
                copy.value = readValue
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = copy.getValue(thisRef, property)
    }

    private var readMutex = Object()

    private val write = Commit(thisContext) {
        synchronized(readMutex) {
            delegates.forEach {
                it.write()
            }
        }
        read.schedule()
    }

    private val read = Commit(copyContext) {
        synchronized(readMutex) {
            delegates.forEach {
                it.read()
            }
        }
        delegates.forEach {
            it.apply()
        }
    }

    private inner class Commit(private val context: CoroutineContext, private val action: () -> Unit) {
        private val scheduled = AtomicBoolean(false)

        fun schedule() {
            if (!scheduled.getAndSet(true)) {
                launch(context, parent = job) {
                    scheduled.set(false)
                    action()
                }
            }
        }
    }
}