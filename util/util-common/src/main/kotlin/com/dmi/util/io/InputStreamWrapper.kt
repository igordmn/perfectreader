package com.dmi.util.io

import java.io.InputStream

abstract class InputStreamWrapper(private val stream: InputStream) : InputStream() {
    override fun available(): Int {
        return stream.available()
    }

    override fun close() {
        stream.close()
    }

    override fun mark(readlimit: Int) {
        stream.mark(readlimit)
    }

    override fun markSupported(): Boolean {
        return stream.markSupported()
    }

    override fun read(buffer: ByteArray): Int {
        return stream.read(buffer)
    }

    override fun read(buffer: ByteArray, byteOffset: Int, byteCount: Int): Int {
        return stream.read(buffer, byteOffset, byteCount)
    }

    @Synchronized
    override fun reset() {
        stream.reset()
    }

    override fun skip(byteCount: Long): Long {
        return stream.skip(byteCount)
    }

    override fun read(): Int {
        return stream.read()
    }
}
