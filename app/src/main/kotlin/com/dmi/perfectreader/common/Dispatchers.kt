package com.dmi.perfectreader.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext

private val nano = newSingleThreadContext("nano")

/**
 * For tasks that run fast (< 1s), but that slow down device if multiple run simultaneously
 */
val Dispatchers.Nano: CoroutineDispatcher get() = nano