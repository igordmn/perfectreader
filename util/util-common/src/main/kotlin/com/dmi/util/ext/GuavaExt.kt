package com.dmi.util.ext

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache

fun <K, V> cache(
        maximumSize: Long = -1,
        softValues: Boolean = false,
        weakValues: Boolean = false,
        weakKeys: Boolean = false,
        load: (K) -> V
): LoadingCache<K, V> {
    val builder = CacheBuilder.newBuilder()
    if (maximumSize >= 0)
        builder.maximumSize(maximumSize)
    if (softValues)
        builder.softValues()
    if (weakValues)
        builder.weakValues()
    if (weakKeys)
        builder.weakKeys()
    return builder.build(cacheLoader(load))
}

fun <K, V> cacheLoader(load: (K) -> V) = CacheLoader.from<K, V> { load(it!!) }