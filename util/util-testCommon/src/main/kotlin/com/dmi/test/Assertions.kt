package com.dmi.test

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals

infix fun Any?.shouldEqual(theOther: Any?) = assertEquals(theOther, this)
infix fun <T> Array<T>?.shouldEqual(theOther: Array<T>?) = assertArrayEquals(theOther, this)
infix fun BooleanArray?.shouldEqual(theOther: BooleanArray?) = assertArrayEquals(theOther, this)
infix fun ByteArray?.shouldEqual(theOther: ByteArray?) = assertArrayEquals(theOther, this)
infix fun ShortArray?.shouldEqual(theOther: ShortArray?) = assertArrayEquals(theOther, this)
infix fun IntArray?.shouldEqual(theOther: IntArray?) = assertArrayEquals(theOther, this)
infix fun LongArray?.shouldEqual(theOther: LongArray?) = assertArrayEquals(theOther, this)
infix fun <T> Iterable<T>?.shouldEqual(theOther: Iterable<T>?) = assertEquals(theOther, this)