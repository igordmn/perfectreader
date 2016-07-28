package com.dmi.perfectreader.test.ndk

import android.support.test.runner.AndroidJUnit4
import android.test.InstrumentationTestCase
import com.dmi.test.shouldEqual
import com.dmi.util.android.test.PaintUtilsTestJNI.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaintUtilsTest : InstrumentationTestCase() {
    @Test
    fun clearSource() {
        // given
        val width = 3
        val height = 4
        val stride = 7
        val data = ByteArray(stride * height) { 6 }

        // when
        clear(width, height, stride, data, 7)

        // then
        for (x in 0..width - 1) {
            for (y in 0..height - 1) {
                data[stride * y + x] shouldEqual 7.toByte()
            }
        }
    }

    @Test
    fun copyPixels() {
        // given
        val dstWidth = 3
        val dstHeight = 4
        val dstStride = 7

        var srcWidth = 2
        var srcHeight = 3
        var srcStride = 5
        var srcData = ByteArray(srcStride * srcHeight) { 6 }

        // when
        var dstData = ByteArray(dstStride * dstHeight) { 3 }
        copyPixels(
                dstWidth, dstHeight, dstStride, dstData,
                srcWidth, srcHeight, srcStride, srcData,
                0, 0
        )

        // then
        dstData[dstStride * 0 + 0] shouldEqual 6.toByte()
        dstData[dstStride * 0 + 1] shouldEqual 6.toByte()
        dstData[dstStride * 0 + 2] shouldEqual 3.toByte()
        dstData[dstStride * 1 + 0] shouldEqual 6.toByte()
        dstData[dstStride * 1 + 1] shouldEqual 6.toByte()
        dstData[dstStride * 1 + 2] shouldEqual 3.toByte()
        dstData[dstStride * 2 + 0] shouldEqual 6.toByte()
        dstData[dstStride * 2 + 1] shouldEqual 6.toByte()
        dstData[dstStride * 2 + 2] shouldEqual 3.toByte()
        dstData[dstStride * 3 + 0] shouldEqual 3.toByte()
        dstData[dstStride * 3 + 1] shouldEqual 3.toByte()
        dstData[dstStride * 3 + 2] shouldEqual 3.toByte()

        // when
        dstData = ByteArray(dstStride * dstHeight) { 3 }
        copyPixels(
                dstWidth, dstHeight, dstStride, dstData,
                srcWidth, srcHeight, srcStride, srcData,
                -1, -2
        )

        // then
        dstData[dstStride * 0 + 0] shouldEqual 6.toByte()
        dstData[dstStride * 0 + 1] shouldEqual 3.toByte()
        dstData[dstStride * 1 + 0] shouldEqual 3.toByte()

        // when
        dstData = ByteArray(dstStride * dstHeight) { 3 }
        copyPixels(
                dstWidth, dstHeight, dstStride, dstData,
                srcWidth, srcHeight, srcStride, srcData,
                -2, -3
        )

        // then
        dstData[dstStride * 0 + 0] shouldEqual 3.toByte()
        dstData[dstStride * 0 + 1] shouldEqual 3.toByte()
        dstData[dstStride * 1 + 0] shouldEqual 3.toByte()

        // when
        dstData = ByteArray(dstStride * dstHeight) { 3 }
        copyPixels(
                dstWidth, dstHeight, dstStride, dstData,
                srcWidth, srcHeight, srcStride, srcData,
                2, 3
        )

        // then
        dstData[dstStride * 3 + 2] shouldEqual 6.toByte()
        dstData[dstStride * 3 + 1] shouldEqual 3.toByte()
        dstData[dstStride * 2 + 2] shouldEqual 3.toByte()

        // when
        dstData = ByteArray(dstStride * dstHeight) { 3 }
        copyPixels(
                dstWidth, dstHeight, dstStride, dstData,
                srcWidth, srcHeight, srcStride, srcData,
                3, 4
        )

        // then
        dstData[dstStride * 3 + 2] shouldEqual 3.toByte()
        dstData[dstStride * 3 + 1] shouldEqual 3.toByte()
        dstData[dstStride * 2 + 2] shouldEqual 3.toByte()

        // when
        srcWidth = 10
        srcHeight = 10
        srcStride = 5
        srcData = ByteArray(srcStride * srcHeight) { 6 }
        dstData = ByteArray(dstStride * dstHeight) { 3 }
        copyPixels(
                dstWidth, dstHeight, dstStride, dstData,
                srcWidth, srcHeight, srcStride, srcData,
                -2, -2
        )

        // then
        for (x in 0..dstWidth - 1) {
            for (y in 0..dstHeight - 1) {
                dstData[dstStride * y + x] shouldEqual 6.toByte()
            }
        }
    }

    @Test
    fun gaussianBlurAdditionalPixels() {
        // expect
        gaussianBlurAdditionalPixels(0F) shouldEqual 0
        gaussianBlurAdditionalPixels(0.1F) shouldEqual 3
        gaussianBlurAdditionalPixels(3F) shouldEqual 9
        gaussianBlurAdditionalPixels(3.3F) shouldEqual 12
        gaussianBlurAdditionalPixels(3.9F) shouldEqual 12
        gaussianBlurAdditionalPixels(4F) shouldEqual 12
        gaussianBlurAdditionalPixels(4.1F) shouldEqual 15
    }

    @Test
    fun gaussianBlur() {
        // given
        val dstWidth = 3
        val dstHeight = 4
        val dstStride = 7

        fun testData(): ByteArray {
            val dstData = ByteArray(dstStride * dstHeight) { 45 }
            dstData[dstStride * 0 + 0] = 10
            dstData[dstStride * 0 + 1] = 20
            dstData[dstStride * 0 + 2] = 30
            dstData[dstStride * 1 + 0] = 40
            dstData[dstStride * 1 + 1] = 50
            dstData[dstStride * 1 + 2] = 60
            dstData[dstStride * 2 + 0] = 70
            dstData[dstStride * 2 + 1] = 80
            dstData[dstStride * 2 + 2] = 90
            dstData[dstStride * 3 + 0] = 91
            dstData[dstStride * 3 + 1] = 94
            dstData[dstStride * 3 + 2] = 99
            return dstData
        }

        fun shrinkData(dstData: ByteArray): ByteArray {
            val dstDataShrinked = ByteArray(dstWidth * dstHeight) { 45 }
            for (x in 0..dstWidth - 1) {
                for (y in 0..dstHeight - 1) {
                    dstDataShrinked[dstWidth * y + x] = dstData[dstStride * y + x]
                }
            }
            return dstDataShrinked
        }

        fun testBlurHorizontal(radius: Float): ByteArray {
            val dstData = testData()
            boxBlurHorizontal(dstWidth, dstHeight, dstStride, dstData, radius)
            return shrinkData(dstData)
        }

        fun testBlurVertical(radius: Float): ByteArray {
            val dstData = testData()
            boxBlurVertical(dstWidth, dstHeight, dstStride, dstData, radius)
            return shrinkData(dstData)
        }

        // data:
        //      10, 20, 30,
        //      40, 50, 60,
        //      70, 80, 90,
        //      91, 94, 99
        testBlurHorizontal(0F) shouldEqual byteArrayOf(
                10, 20, 30,
                40, 50, 60,
                70, 80, 90,
                91, 94, 99
        )
        testBlurVertical(0F) shouldEqual byteArrayOf(
                10, 20, 30,
                40, 50, 60,
                70, 80, 90,
                91, 94, 99
        )

        // data:
        //      10, 20, 30,
        //      40, 50, 60,
        //      70, 80, 90,
        //      91, 94, 99
        testBlurHorizontal(1F) shouldEqual byteArrayOf(
                (10 + 20) / 3, (10 + 20 + 30) / 3, (20 + 30) / 3,
                (40 + 50) / 3, (40 + 50 + 60) / 3, (50 + 60) / 3,
                (70 + 80) / 3, (70 + 80 + 90) / 3, (80 + 90) / 3,
                (91 + 94) / 3, (91 + 94 + 99) / 3, (94 + 99) / 3
        )
        testBlurVertical(1F) shouldEqual byteArrayOf(
                (0 + 10 + 40) / 3, (0 + 20 + 50) / 3, (0 + 30 + 60) / 3,
                (10 + 40 + 70) / 3, (20 + 50 + 80) / 3, (30 + 60 + 90) / 3,
                (40 + 70 + 91) / 3, (50 + 80 + 94) / 3, (60 + 90 + 99) / 3,
                (70 + 91 + 0) / 3, (80 + 94 + 0) / 3, (90 + 99 + 0) / 3
        )

        // data:
        //      10, 20, 30,
        //      40, 50, 60,
        //      70, 80, 90,
        //      91, 94, 99
        testBlurHorizontal(2F) shouldEqual byteArrayOf(
                (10 + 20 + 30) / 5, (10 + 20 + 30) / 5, (10 + 20 + 30) / 5,
                (40 + 50 + 60) / 5, (40 + 50 + 60) / 5, (40 + 50 + 60) / 5,
                (70 + 80 + 90) / 5, (70 + 80 + 90) / 5, (70 + 80 + 90) / 5,
                (91 + 94 + 99) / 5, (91 + 94 + 99) / 5, (91 + 94 + 99) / 5
        )
        testBlurVertical(2F) shouldEqual byteArrayOf(
                (0 + 10 + 40 + 70) / 5, (0 + 20 + 50 + 80) / 5, (0 + 30 + 60 + 90) / 5,
                (10 + 40 + 70 + 91) / 5, (20 + 50 + 80 + 94) / 5, (30 + 60 + 90 + 99) / 5,
                (10 + 40 + 70 + 91) / 5, (20 + 50 + 80 + 94) / 5, (30 + 60 + 90 + 99) / 5,
                (40 + 70 + 91 + 0) / 5, (50 + 80 + 94 + 0) / 5, (60 + 90 + 99 + 0) / 5
        )

        // data:
        //      10, 20, 30,
        //      40, 50, 60,
        //      70, 80, 90,
        //      91, 94, 99
        testBlurHorizontal(7F) shouldEqual byteArrayOf(
                (10 + 20 + 30) / 15, (10 + 20 + 30) / 15, (10 + 20 + 30) / 15,
                (40 + 50 + 60) / 15, (40 + 50 + 60) / 15, (40 + 50 + 60) / 15,
                (70 + 80 + 90) / 15, (70 + 80 + 90) / 15, (70 + 80 + 90) / 15,
                (91 + 94 + 99) / 15, (91 + 94 + 99) / 15, (91 + 94 + 99) / 15
        )
        testBlurVertical(7F) shouldEqual byteArrayOf(
                (10 + 40 + 70 + 91) / 15, (20 + 50 + 80 + 94) / 15, (30 + 60 + 90 + 99) / 15,
                (10 + 40 + 70 + 91) / 15, (20 + 50 + 80 + 94) / 15, (30 + 60 + 90 + 99) / 15,
                (10 + 40 + 70 + 91) / 15, (20 + 50 + 80 + 94) / 15, (30 + 60 + 90 + 99) / 15,
                (10 + 40 + 70 + 91) / 15, (20 + 50 + 80 + 94) / 15, (30 + 60 + 90 + 99) / 15
        )

        // data:
        //      10, 20, 30,
        //      40, 50, 60,
        //      70, 80, 90,
        //      91, 94, 99
        testBlurHorizontal(0.3F) shouldEqual byteArrayOf(
                ((10 + 20 * 0.3) / 1.6).toByte(), ((10 * 0.3 + 20 + 30 * 0.3) / 1.6).toByte(), ((20 * 0.3 + 30) / 1.6).toByte(),
                ((40 + 50 * 0.3) / 1.6).toByte(), ((40 * 0.3 + 50 + 60 * 0.3) / 1.6).toByte(), ((50 * 0.3 + 60) / 1.6).toByte(),
                ((70 + 80 * 0.3) / 1.6).toByte(), ((70 * 0.3 + 80 + 90 * 0.3) / 1.6).toByte(), ((80 * 0.3 + 90) / 1.6).toByte(),
                ((91 + 94 * 0.3) / 1.6).toByte(), ((91 * 0.3 + 94 + 99 * 0.3) / 1.6).toByte(), ((94 * 0.3 + 99) / 1.6).toByte()
        )
        testBlurVertical(0.3F) shouldEqual byteArrayOf(
                ((0 * 0.3 + 10 + 40 * 0.3) / 1.6).toByte(), ((0 * 0.3 + 20 + 50 * 0.3) / 1.6).toByte(), ((0 * 0.3 + 30 + 60 * 0.3) / 1.6).toByte(),
                ((10 * 0.3 + 40 + 70 * 0.3) / 1.6).toByte(), ((20 * 0.3 + 50 + 80 * 0.3) / 1.6).toByte(), ((30 * 0.3 + 60 + 90 * 0.3) / 1.6).toByte(),
                ((40 * 0.3 + 70 + 91 * 0.3) / 1.6).toByte(), ((50 * 0.3 + 80 + 94 * 0.3) / 1.6).toByte(), ((60 * 0.3 + 90 + 99 * 0.3) / 1.6).toByte(),
                ((70 * 0.3 + 91 + 0 * 0.3) / 1.6).toByte(), ((80 * 0.3 + 94 + 0 * 0.3) / 1.6).toByte(), ((90 * 0.3 + 99 + 0 * 0.3) / 1.6).toByte()
        )

        // data:
        //      10, 20, 30,
        //      40, 50, 60,
        //      70, 80, 90,
        //      91, 94, 99
        testBlurHorizontal(1.7F) shouldEqual byteArrayOf(
                ((10 + 20 + 30 * 0.7) / 4.4).toByte(), ((10 + 20 + 30) / 4.4).toByte(), ((10 * 0.7 + 20 + 30) / 4.4).toByte(),
                ((40 + 50 + 60 * 0.7) / 4.4).toByte(), ((40 + 50 + 60) / 4.4).toByte(), ((40 * 0.7 + 50 + 60) / 4.4).toByte(),
                ((70 + 80 + 90 * 0.7) / 4.4).toByte(), ((70 + 80 + 90) / 4.4).toByte(), ((70 * 0.7 + 80 + 90) / 4.4).toByte(),
                ((91 + 94 + 99 * 0.7) / 4.4).toByte(), ((91 + 94 + 99) / 4.4).toByte(), ((91 * 0.7 + 94 + 99) / 4.4).toByte()
        )
        testBlurVertical(1.7F) shouldEqual byteArrayOf(
                ((0 + 10 + 40 + 70 * 0.7) / 4.4).toByte(), ((0 + 20 + 50 + 80 * 0.7) / 4.4).toByte(), ((0 + 30 + 60 + 90 * 0.7) / 4.4).toByte(),
                ((10 + 40 + 70 + 91 * 0.7) / 4.4).toByte(), ((20 + 50 + 80 + 94 * 0.7) / 4.4).toByte(), ((30 + 60 + 90 + 99 * 0.7) / 4.4).toByte(),
                ((10 * 0.7 + 40 + 70 + 91) / 4.4).toByte(), ((20 * 0.7 + 50 + 80 + 94) / 4.4).toByte(), ((30 * 0.7 + 60 + 90 + 99) / 4.4).toByte(),
                ((40 * 0.7 + 70 + 91 + 0) / 4.4).toByte(), ((50 * 0.7 + 80 + 94 + 0) / 4.4).toByte(), ((60 * 0.7 + 90 + 99 + 0) / 4.4).toByte()
        )

        // test overflow
        var dstData = ByteArray(dstStride * dstHeight) { 45 }
        dstData[dstStride * 0 + 0] = 127
        dstData[dstStride * 0 + 1] = 127
        dstData[dstStride * 0 + 2] = 127
        dstData[dstStride * 1 + 0] = 127
        dstData[dstStride * 1 + 1] = 127
        dstData[dstStride * 1 + 2] = 127
        dstData[dstStride * 2 + 0] = 127
        dstData[dstStride * 2 + 1] = 127
        dstData[dstStride * 2 + 2] = 127
        dstData[dstStride * 3 + 0] = 127
        dstData[dstStride * 3 + 1] = 127
        dstData[dstStride * 3 + 2] = 127
        boxBlurHorizontal(dstWidth, dstHeight, dstStride, dstData, 10F)
        shrinkData(dstData) shouldEqual byteArrayOf(
                127 * 3 / 21, 127 * 3 / 21, 127 * 3 / 21,
                127 * 3 / 21, 127 * 3 / 21, 127 * 3 / 21,
                127 * 3 / 21, 127 * 3 / 21, 127 * 3 / 21,
                127 * 3 / 21, 127 * 3 / 21, 127 * 3 / 21
        )

        // test zero
        dstData = ByteArray(0)
        boxBlurHorizontal(0, 0, 56, dstData, 10F)
    }
}