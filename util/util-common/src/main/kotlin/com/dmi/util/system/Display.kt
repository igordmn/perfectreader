package com.dmi.util.system

interface Display {
    val currentTime: Nanos
    suspend fun waitVSyncTime(): Nanos
}