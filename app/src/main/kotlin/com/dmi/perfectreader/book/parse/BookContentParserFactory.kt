package com.dmi.perfectreader.book.parse

import android.net.Uri
import com.dmi.util.log.Log
import com.google.common.io.Files
import java.io.File

class BookContentParserFactory(log: Log, config: ParseConfig) {
    private val charsetDetector = CharsetDetector(log, config.defaultCharset)

    fun parserFor(uri: Uri): BookContentParser {
        val extension = uri.path.substringAfterLast('.')
        val source = sourceFor(uri)
        return when (extension) {
            "txt" -> TXTContentParser(charsetDetector, source)
            else -> error("Unsupported format")
        }
    }

    private fun sourceFor(uri: Uri) = when (uri.scheme) {
        "file" -> Files.asByteSource(File(uri.path))
        else -> error("Unsupported scheme")
    }
}