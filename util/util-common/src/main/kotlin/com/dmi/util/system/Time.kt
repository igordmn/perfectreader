package com.dmi.util.system

typealias Nanos = Long

fun Nanos.toMillis(): Double = this / 1E6
fun Nanos.toSeconds(): Double = this / 1E9
fun Nanos.toMinutes(): Double =  this / 1E9 / 60.0
fun seconds(seconds: Double): Nanos = (seconds * 1E9).toLong()
fun minutes(minutes: Double): Nanos = (minutes * 1E9 * 60).toLong()