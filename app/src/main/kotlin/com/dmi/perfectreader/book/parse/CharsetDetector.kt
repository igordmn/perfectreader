package com.dmi.perfectreader.book.parse

import com.dmi.util.log
import com.google.common.io.ByteSource
import org.mozilla.universalchardet.UniversalDetector
import java.nio.charset.Charset

class CharsetDetector(private val charsetConfig: ParseConfig.Charset) {
    companion object {
        private val BUFFER = ByteArray(10000)
        private val mozillaDetector = UniversalDetector(null)
    }

    fun detect(source: ByteSource) = when (charsetConfig) {
        is ParseConfig.Charset.Auto -> autoDetect(source)
        is ParseConfig.Charset.Fixed -> parseCharset(charsetConfig.name)
    }

    fun autoDetect(source: ByteSource): Charset {
        try {
            source.openBufferedStream().use {
                val size = it.read(BUFFER)
                mozillaDetector.handleData(BUFFER, 0, size)
                mozillaDetector.dataEnd()
            }
            val detectedCharset = mozillaDetector.detectedCharset
            return if (detectedCharset != null) parseCharset(detectedCharset) else Charsets.UTF_8
        } catch (e: Exception) {
            log.e(e, "Error detecting charset")
            return Charsets.UTF_8
        } finally {
            mozillaDetector.reset()
        }
    }

    private fun parseCharset(charsetName: String) = try {
        Charset.forName(charsetName)
    } catch (e: Exception) {
        log.e(e, "Charset not found: $charsetName")
        Charsets.UTF_8
    }
}