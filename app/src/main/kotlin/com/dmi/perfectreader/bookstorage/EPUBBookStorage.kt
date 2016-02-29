package com.dmi.perfectreader.bookstorage

import com.dmi.perfectreader.cache.BookResourceCache
import com.dmi.util.cache.DataCache
import com.google.common.base.Preconditions.checkState
import com.google.common.io.ByteStreams
import org.readium.sdk.android.EPub3
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream
import java.lang.String.format
import java.util.*
import java.util.zip.ZipFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EPUBBookStorage : BookStorage {
    @Inject
    protected lateinit  var resourceCache: BookResourceCache

    private var bookFile: File? = null
    override lateinit var segmentURLs: Array<String>
    override lateinit var segmentSizes: IntArray
    private var loaded = false

    fun load(bookFile: File) {
        this.bookFile = bookFile
        val segmentPaths = loadSegmentPaths(bookFile)
        segmentURLs = toUrls(segmentPaths)
        segmentSizes = loadLengths(bookFile, segmentPaths)
        loaded = true
    }

    override fun readURL(url: String): InputStream {
        checkState(loaded)
        if (!url.startsWith(URL_PREFIX)) {
            throw SecurityException()
        }
        val key = BookResourceCache.resourceKey(bookFile!!.absolutePath, url, bookFile!!.lastModified())
        return resourceCache.openRead(key, object: DataCache.DataWriter {
            override fun write(outputStream: OutputStream) {
                if (!bookFile!!.exists()) {
                    throw FileNotFoundException(format("Book file not found: %s", bookFile!!.absolutePath))
                }
                ZipFile(bookFile).use { zipFile ->
                    val filePath = url.substring(URL_PREFIX.length)
                    val entry = zipFile.getEntry(filePath) ?: throw FileNotFoundException("ZipEntry $filePath not found")
                    zipFile.getInputStream(entry).use { inputStream -> ByteStreams.copy(inputStream, outputStream) }
                }
            }
        })
    }

    companion object {
        private val URL_PREFIX = "bookstorage://"

        private fun loadSegmentPaths(bookFile: File): Array<String> {
            val paths = ArrayList<String>()
            if (!bookFile.exists()) {
                throw FileNotFoundException(format("Book file not found: %s", bookFile.absolutePath))
            }
            val container = EPub3.openBook(bookFile.absolutePath)
            try {
                val pack = container.defaultPackage
                val basePath = pack.basePath
                for (spineItem in pack.spineItems) {
                    paths.add(basePath + spineItem.href)
                }
            } finally {
                EPub3.closeBook(container)
            }
            return paths.toArray<String>(arrayOfNulls<String>(paths.size))
        }

        private fun toUrls(paths: Array<String>): Array<String> {
            return paths.toList().map { URL_PREFIX + it }.toTypedArray()
        }

        private fun loadLengths(zipFile: File, paths: Array<String>): IntArray {
            val fileLengths = IntArray(paths.size)
            ZipFile(zipFile).use { zip ->
                for (i in paths.indices) {
                    val entry = zip.getEntry(paths[i]) ?: throw FileNotFoundException("ZipEntry " + paths[i] + " not found")
                    fileLengths[i] = entry.size.toInt()
                }
            }
            return fileLengths
        }
    }
}
