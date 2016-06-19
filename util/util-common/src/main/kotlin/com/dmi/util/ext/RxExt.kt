package com.dmi.util.ext

import rx.Observable
import rx.Scheduler

@Suppress("NOTHING_TO_INLINE")
inline fun <R> LambdaObservable(noinline action: () -> R) = Observable.fromCallable<R>(action)

fun async(scheduler: Scheduler, run: () -> Unit) = LambdaObservable(run).subscribeOn(scheduler).subscribe()

infix fun <T> Observable<T>.merge(other: Observable<T>) = this.mergeWith(other)