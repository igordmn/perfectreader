package com.dmi.util.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext

private val heavy = newSingleThreadContext("heavy")

val Dispatchers.Heavy: CoroutineDispatcher get() = heavy