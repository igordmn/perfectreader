package com.dmi.perfectreader.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext

private val nano = newSingleThreadContext("nano")

/**
 * For tasks, which executes fast (< 1s), but they slow down device if multiple run simultaneously
 */
val Dispatchers.Nano: CoroutineDispatcher get() = nano