package com.dmi.perfectreader.book.parse

import com.dmi.util.log.Log
import com.google.common.io.ByteSource
import org.mozilla.universalchardet.UniversalDetector
import java.nio.charset.Charset
import java.util.regex.Pattern

class CharsetDetector(private val log: Log) {
    private val xmlPattern = Pattern.compile("^\\s*<\\?xml.*encoding\\s*=\\s*\"(.*)\".*?>")

    companion object {
        private val BUFFER = ByteArray(10000)
        private val mozillaDetector = UniversalDetector(null)
    }

    fun detect(source: ByteSource): Charset = autoDetect(source)

    fun detectForXML(source: ByteSource): Charset {
        return readXMLEncoding(source) ?: detect(source)
    }

    private fun readXMLEncoding(source: ByteSource): Charset? {
        source.openStream().bufferedReader().use {
            val buffer = CharArray(256)
            val length = it.read(buffer)
            val str = String(buffer, 0, length)
            val matcher = xmlPattern.matcher(str)

            return if (matcher.find()) {
                try {
                    Charset.forName(matcher.group(1))
                } catch (e: Exception) {
                    log.e(e, "Error detecting xml charset")
                    null
                }
            } else {
                null
            }
        }
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