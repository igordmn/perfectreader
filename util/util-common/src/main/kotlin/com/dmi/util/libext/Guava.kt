package com.dmi.util.libext

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader

fun cacheBuilder() = CacheBuilder.newBuilder()
fun <K, V> cacheLoader(load: (K) -> V) = CacheLoader.from<K, V> { load(it!!) }
fun <K, V> weakValuesCache(load: (K) -> V) = cacheBuilder().weakValues().build(cacheLoader(load))