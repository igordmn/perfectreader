package com.dmi.perfectreader.layout.config

import java.io.InputStream

interface ResourceLoader {
    fun load(src: String): InputStream
}
