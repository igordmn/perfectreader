package com.dmi.perfectreader.fragment.book.page

import com.dmi.perfectreader.app.pageLoadScheduler
import com.dmi.perfectreader.fragment.book.layout.pagination.LocatedSequence
import com.dmi.perfectreader.fragment.book.layout.pagination.Page
import com.dmi.util.collection.SequenceEntry
import com.dmi.util.ext.LambdaObservable
import com.dmi.util.mainScheduler
import rx.Subscription
import rx.lang.kotlin.PublishSubject


class PagesLoader(
        private val pages: Pages,
        private val pageSequence: LocatedSequence<Page>
) {
    val onLoad = PublishSubject<Unit>()

    private var loadSubscription: Subscription? = null

    fun destroy() {
        loadSubscription?.unsubscribe()
        loadSubscription = null
    }

    fun check() {
        fun schedule(
                load: () -> SequenceEntry<Page>?,
                setPage: (SequenceEntry<Page>?) -> Unit
        ) {
            loadSubscription = LambdaObservable(load)
                    .subscribeOn(pageLoadScheduler)
                    .observeOn(mainScheduler)
                    .subscribe { entry ->
                        setPage(entry)
                        loadSubscription = null
                        check()
                    }
        }

        fun loadCurrent() {
            val location = pages.location
            val load = { pageSequence[location] }
            schedule(load) {
                if (pages.location == location) {
                    pages.setLoadedCurrent(it)
                    onLoad.onNext(Unit)
                }
            }
        }

        fun loadRight() {
            val lastEntry = pages.lastLoadedEntry
            val load = { lastEntry?.nextOrNull }
            schedule(load) {
                if (!pages.isRightLoaded && pages.lastLoadedEntry == lastEntry) {
                    pages.addLoadedRight(it)
                    onLoad.onNext(Unit)
                }
            }
        }

        fun loadLeft() {
            val firstEntry = pages.firstLoadedEntry
            val load = { firstEntry?.previousOrNull }
            schedule(load) {
                if (!pages.isLeftLoaded && pages.firstLoadedEntry == firstEntry) {
                    pages.addLoadedLeft(it)
                    onLoad.onNext(Unit)
                }
            }
        }

        if (!isLoading() && !pages.isLoaded) {
            when {
                !pages.isCurrentLoaded -> loadCurrent()
                pages.leftLoadedCount >= pages.rightLoadedCount -> loadRight()
                pages.leftLoadedCount < pages.rightLoadedCount -> loadLeft()
            }
        }
    }

    private fun isLoading() = loadSubscription != null
}