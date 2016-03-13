package com.dmi.perfectreader.layout.paragraph

interface HangingConfig {
    fun leftHangFactor(ch: Char): Float
    fun rightHangFactor(ch: Char): Float
}
