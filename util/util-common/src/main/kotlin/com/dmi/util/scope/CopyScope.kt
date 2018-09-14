package com.dmi.util.scope

import com.dmi.util.coroutine.threadContext
import com.dmi.util.lang.ReadWriteProperty2
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
        val cached by scope.cached(compute)
        lateinit var copy: ReadWriteProperty2<Any?, T>

        private var changed = false

        @Volatile
        private var writeValue: T = cached

        @Volatile
        private var readValue: T = cached

        @Volatile
        private var writeChanged = false

        @Volatile
        private var readChanged = false

        init {
            delegates.add(this)

            launch(copyContext, parent = job) {
                copy = observable(readValue)
            }

            // it safe subscribe without dispose, because subscription will be disposed in scope.dispose
            onchange { cached }.subscribe {
                changed = true
                write.schedule()
            }
        }

        fun write() {
            if (changed)
                writeValue = cached
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

        override fun getValue(thisRef: Any?, property: KProperty<*>): T = copy.value
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