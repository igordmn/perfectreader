package com.dmi.perfectreader.cache

import android.content.Context
import com.dmi.perfectreader.BuildConfig.VERSION_CODE
import com.dmi.util.availableCacheDir
import com.dmi.util.cache.DataCache
import com.dmi.util.cache.DiskDataCache
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class BookResourceCache : DataCache {

    private var diskDataCache: DiskDataCache? = null
    private var currentCacheDir: File? = null

    @Inject
    @Named("applicationContext")
    protected lateinit  var context: Context

    override fun openRead(key: String, writeData: (OutputStream) -> Unit): InputStream {
        val cacheDir = cacheDir
        if (cacheDir != currentCacheDir && diskDataCache != null) {
            diskDataCache!!.close()
            diskDataCache = null
        }
        if (diskDataCache == null) {
            diskDataCache = DiskDataCache(cacheDir, VERSION_CODE, MAX_SIZE)
        }
        currentCacheDir = cacheDir
        return diskDataCache!!.openRead(key, writeData)
    }

    private val cacheDir: File
        get() = File(context.availableCacheDir(), CACHE_DIR)

    companion object {
        private val CACHE_DIR = "bookResource"
        private val MAX_SIZE = 32 * 1024 * 1024 // 32 MB

        fun resourceKey(bookFilePath: String, innerResourcePath: String, lastModified: Long): String {
            return "file: $bookFilePath; url: $innerResourcePath; lastModified: $lastModified"
        }
    }
}