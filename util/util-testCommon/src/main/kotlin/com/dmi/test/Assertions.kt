package com.dmi.test

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals

infix fun Any?.shouldEqual(other: Any?) = assertEquals(other, this)
infix fun <T> Array<T>?.shouldEqual(other: Array<T>?) = assertArrayEquals(other, this)
infix fun BooleanArray?.shouldEqual(other: BooleanArray?) = assertArrayEquals(other, this)
infix fun ByteArray?.shouldEqual(other: ByteArray?) = assertArrayEquals(other, this)
infix fun ShortArray?.shouldEqual(other: ShortArray?) = assertArrayEquals(other, this)
infix fun IntArray?.shouldEqual(other: IntArray?) = assertArrayEquals(other, this)
infix fun LongArray?.shouldEqual(other: LongArray?) = assertArrayEquals(other, this)
infix fun <T> Iterable<T>?.shouldEqual(other: Iterable<T>?) = assertEquals(other, this)

infix fun Double?.shouldNearThreePrecision(other: Double) = assertEquals(other, this!!.toDouble(), 0.001)