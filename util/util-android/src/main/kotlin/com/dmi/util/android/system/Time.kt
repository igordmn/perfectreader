package com.dmi.util.android.system

fun doubleSeconds(nanoSeconds: Long): Double = nanoSeconds / 1E9
fun nanoSeconds(seconds: Double): Long = (seconds * 1E9).toLong()