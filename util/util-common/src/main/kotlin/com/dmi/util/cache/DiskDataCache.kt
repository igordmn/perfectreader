package com.dmi.util.cache

import com.google.common.base.Charsets.UTF_8
import com.google.common.hash.Hashing.sha1
import com.jakewharton.disklrucache.DiskLruCache
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class DiskDataCache(directory: File, appVersion: Int, maxSize: Int) : DataCache, Closeable {
    private val diskLruCache = DiskLruCache.open(directory, appVersion, 1, maxSize.toLong())

    override fun close() = diskLruCache.close()

    override fun openRead(key: String, writeData: (OutputStream) -> Unit): InputStream {
        val hash = hashKey(key)
        val snapshot = diskLruCache.get(hash)
        if (snapshot != null) {
            return snapshot.getInputStream(0)
        } else {
            val editor = diskLruCache.edit(hash)
            editor.newOutputStream(0).use { writeData(it) }
            editor.commit()
            diskLruCache.flush()
            return diskLruCache.get(hash).getInputStream(0)
        }
    }

    private fun hashKey(key: String) = sha1().hashString(key, UTF_8).toString()
}
