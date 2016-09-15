package com.dmi.util.ext

import rx.Observable
import rx.Scheduler
import rx.Subscription
import java.util.concurrent.TimeUnit

@Suppress("NOTHING_TO_INLINE")
inline fun <R> LambdaObservable(noinline action: () -> R): Observable<R> = Observable.fromCallable<R>(action)

fun async(scheduler: Scheduler, run: () -> Unit): Subscription = LambdaObservable(run).subscribeOn(scheduler).subscribe()

infix fun <R> Observable<R>.merge(other: Observable<R>): Observable<R> = this.mergeWith(other)

fun delay(millis: Long, scheduler: Scheduler, action: () -> Unit): Subscription =
        Observable.timer(millis, TimeUnit.MILLISECONDS, scheduler).subscribe {
            action()
        }

fun repeat(millis: Long, scheduler: Scheduler, action: () -> Unit): Subscription =
        Observable.interval(millis, TimeUnit.MILLISECONDS, scheduler).subscribe {
            action()
        }