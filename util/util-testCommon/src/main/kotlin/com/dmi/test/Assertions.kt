package com.dmi.test

import org.junit.Assert.*

infix fun Any?.shouldBe(other: Any?) = assertEquals(other, this)
infix fun <T> Array<T>?.shouldBe(other: Array<T>?) = assertArrayEquals(other, this)
infix fun BooleanArray?.shouldBe(other: BooleanArray?) = assertArrayEquals(other, this)
infix fun ByteArray?.shouldBe(other: ByteArray?) = assertArrayEquals(other, this)
infix fun ShortArray?.shouldBe(other: ShortArray?) = assertArrayEquals(other, this)
infix fun IntArray?.shouldBe(other: IntArray?) = assertArrayEquals(other, this)
infix fun LongArray?.shouldBe(other: LongArray?) = assertArrayEquals(other, this)
infix fun <T> Iterable<T>?.shouldBe(other: Iterable<T>?) = assertEquals(other, this)

infix fun Double?.shouldNearThreePrecision(other: Double) = assertEquals(other, this!!.toDouble(), 0.001)

inline fun <reified T> shouldThrow(action: () -> Unit) {
    var throwed: Throwable? = null
    try {
        action()
    } catch (e: Throwable) {
        throwed = e
    }
    val cls = T::class.java
    assertNotNull("Exception $cls not throwed", throwed)
    assertEquals(T::class.java, throwed!!.javaClass)
}