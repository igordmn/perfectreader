package com.dmi.util.concurrent

import com.dmi.util.ext.LambdaObservable
import rx.Scheduler
import rx.lang.kotlin.PublishSubject

/**
 * Загружает какой-то ресурс в отдельном потоке.
 * Когда ресурс готов, срабатывает onReady
 * Его можно забрать функцией completeIfReady
 * Можно перезагрузить ресурс, вызвав повторно schedule.
 * Перезагрузка ресурса начнется только после того, как завершится предыдущая загрузка (вызовется completeIfReady)
 * Если есть какя-нибудь загрузка в очереди, и она еще не началась, то, при вызове нового schedule, загрузка, находящаяся в очереди, заместится новой загрузкой
 * destroyResult - освобождение ресурсов, используемых при загрузке
 *
 * Load может произойти и после loader.destroy, но его результат не будет передан дальше
 * Все методы должны вызываться из одного потока, за исключением того, что onReady будет вызван из другого потока
 */
@Suppress("ProtectedInFinal")
class SingleResourceLoader<R>(
        private val scheduler: Scheduler,
        private val destroyResult: (R) -> Unit
) {
    val onReady = PublishSubject<Unit>()

    protected var queuedLoad: (() -> R)? = null
    protected var isLoading = false
    protected @Volatile var readyResult: R? = null
    protected @Volatile var destroyed = false

    fun schedule(load: () -> R) {
        require(!destroyed)

        if (isLoading) {
            queuedLoad = load
        } else {
            isLoading = true
            startLoad(load)
        }
    }

    protected fun startLoad(load: () -> R) {
        LambdaObservable {
            load()
        }.subscribeOn(scheduler).subscribe { result ->
            if (destroyed) {
                destroyResult(result)
            } else {
                readyResult = result
                onReady.onNext(Unit)
            }
        }
    }

    fun destroy() {
        require(!destroyed)

        destroyed = true
        readyResult?.let {
            destroyResult(it)
        }
    }

    inline fun completeIfReady(action: (R) -> Unit) {
        require(!destroyed)

        readyResult?.let {
            action(it)
            destroyResult(it)
            readyResult = null

            if (queuedLoad != null) {
                startLoad(queuedLoad!!)
                queuedLoad = null
            } else {
                isLoading = false
            }
        }
    }
}