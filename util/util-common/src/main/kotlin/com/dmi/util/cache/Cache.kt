package com.dmi.util.cache

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader

interface Cache<K, V> {
    operator fun get(key: K): V
}

private data class KeyHolder<T>(val key: T)

fun <K, V> cache(
        maximumSize: Long = -1,
        softValues: Boolean = false,
        weakValues: Boolean = false,
        weakKeys: Boolean = false,
        load: (K) -> V
): Cache<K, V> {
    val builder = CacheBuilder.newBuilder()
    if (maximumSize >= 0)
        builder.maximumSize(maximumSize)
    if (softValues)
        builder.softValues()
    if (weakValues)
        builder.weakValues()
    if (weakKeys)
        builder.weakKeys()
    val guavaCache = builder.build(cacheLoader(load))
    return object : Cache<K, V> {
        override fun get(key: K) = guavaCache[KeyHolder(key)]
    }
}

private fun <K, V> cacheLoader(load: (K) -> V) = CacheLoader.from<KeyHolder<K>, V> { load(it!!.key) }