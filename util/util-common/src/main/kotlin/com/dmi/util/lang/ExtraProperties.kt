package com.dmi.util.lang

import com.dmi.util.cache.cache
import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private typealias ExtraProperties = IdentityHashMap<Any, Any>

private val extra: Cache<Any, ExtraProperties> = CacheBuilder
        .newBuilder()
        .weakKeys()
        .build<Any, ExtraProperties>()

@Suppress("UNCHECKED_CAST")
fun <R, T> extra(create: R.() -> T) = object : ReadOnlyProperty<R, T> {
    override fun getValue(thisRef: R, property: KProperty<*>): T {
        val properties = extra.get(thisRef, ::ExtraProperties)
        return synchronized(properties) {
            properties.getOrPut(property) { thisRef.create() as Any } as T
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <R, K, V> extraCache(load: R.(K) -> V) = extra<R, com.dmi.util.cache.Cache<K,V>> { cache { load(it) } }