package com.dmi.util.rx

import com.dmi.util.ext.LambdaObservable
import rx.Scheduler

fun runOn(scheduler: Scheduler, action: () -> Unit) = LambdaObservable { action() }.subscribeOn(scheduler).subscribe()