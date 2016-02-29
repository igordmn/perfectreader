package com.dmi.util.cache


import com.dmi.util.io.InputStreamWrapper
import com.google.common.base.Charsets
import com.google.common.hash.Hashing.sha1
import com.jakewharton.disklrucache.DiskLruCache
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.InputStream

class DiskDataCache @Throws(IOException::class)
constructor(directory: File, appVersion: Int, maxSize: Int) : DataCache, Closeable {
    private val diskLruCache: DiskLruCache

    init {
        diskLruCache = DiskLruCache.open(directory, appVersion, 1, maxSize.toLong())
    }

    @Throws(IOException::class)
    override fun close() {
        diskLruCache.close()
    }

    @Throws(IOException::class)
    override fun openRead(key: String, dataWriter: DataCache.DataWriter): InputStream {
        var key = key
        key = hashKey(key)
        val snapshot = diskLruCache.get(key)
        if (snapshot != null) {
            return SnapshotInputStream(snapshot, 0)
        } else {
            val editor = diskLruCache.edit(key)
            editor.newOutputStream(0).use { os -> dataWriter.write(os) }
            editor.commit()
            diskLruCache.flush()
            return SnapshotInputStream(diskLruCache.get(key), 0)
        }
    }

    private fun hashKey(key: String): String {
        return sha1().hashString(key, Charsets.UTF_8).toString()
    }

    private class SnapshotInputStream constructor(private val snapshot: DiskLruCache.Snapshot, index: Int) : InputStreamWrapper(snapshot.getInputStream(index)) {

        @Throws(IOException::class)
        override fun close() {
            super.close()
            snapshot.close()
        }
    }
}
