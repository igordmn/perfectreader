package com.dmi.util.ext

import rx.Observable
import rx.Scheduler
import rx.Subscription

@Suppress("NOTHING_TO_INLINE")
inline fun <R> LambdaObservable(noinline action: () -> R): Observable<R> = Observable.fromCallable<R>(action)

fun async(scheduler: Scheduler, run: () -> Unit): Subscription = LambdaObservable(run).subscribeOn(scheduler).subscribe()

infix fun <R> Observable<R>.merge(other: Observable<R>): Observable<R> = this.mergeWith(other)