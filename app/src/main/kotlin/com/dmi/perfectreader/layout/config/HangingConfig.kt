package com.dmi.perfectreader.layout.config

interface HangingConfig {
    fun leftHangFactor(ch: Char): Float
    fun rightHangFactor(ch: Char): Float
}
