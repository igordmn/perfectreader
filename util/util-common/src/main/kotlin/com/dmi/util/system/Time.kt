package com.dmi.util.system

typealias Nanos = Long

fun Nanos.toSeconds(): Double = this / 1E9
fun seconds(seconds: Double): Nanos = (seconds * 1E9).toLong()