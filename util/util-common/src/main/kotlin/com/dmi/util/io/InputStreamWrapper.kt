package com.dmi.util.io

import java.io.IOException
import java.io.InputStream

abstract class InputStreamWrapper(private val stream: InputStream) : InputStream() {

    @Throws(IOException::class)
    override fun available(): Int {
        return stream.available()
    }

    @Throws(IOException::class)
    override fun close() {
        stream.close()
    }

    override fun mark(readlimit: Int) {
        stream.mark(readlimit)
    }

    override fun markSupported(): Boolean {
        return stream.markSupported()
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray): Int {
        return stream.read(buffer)
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, byteOffset: Int, byteCount: Int): Int {
        return stream.read(buffer, byteOffset, byteCount)
    }

    @Synchronized @Throws(IOException::class)
    override fun reset() {
        stream.reset()
    }

    @Throws(IOException::class)
    override fun skip(byteCount: Long): Long {
        return stream.skip(byteCount)
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return stream.read()
    }
}
