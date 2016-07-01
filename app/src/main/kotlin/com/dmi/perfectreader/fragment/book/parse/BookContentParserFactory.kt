package com.dmi.perfectreader.fragment.book.parse

import android.content.Context
import android.net.Uri
import com.dmi.util.android.io.AssetsFileSource
import com.google.common.io.Files
import java.io.File

class BookContentParserFactory(
        private val config: ParseConfig,
        private val context: Context
) {
    private val charsetDetector = CharsetDetector(config.defaultCharset)

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
        "assets" -> AssetsFileSource(context.assets, uri.path.substring(1))
        else -> error("Unsupported scheme")
    }
}