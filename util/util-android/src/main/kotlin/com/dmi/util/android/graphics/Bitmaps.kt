package com.dmi.util.android.graphics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.dmi.util.coroutine.Heavy
import com.dmi.util.graphic.Size
import com.google.common.io.ByteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.InputStream

// todo use inSampleSize for faster loading and low memory consumption
suspend fun InputStream.toBitmap(maxSize: Size): Bitmap = use { stream ->
    withContext(Dispatchers.Heavy) {
        val original = BitmapFactory.decodeStream(stream)
        val originalAspect = original.width.toFloat() / original.height
        val maxAspect = maxSize.width.toFloat() / maxSize.height
        val scaledWidth: Int
        val scaledHeight: Int
        if (originalAspect > maxAspect) {
            scaledWidth = maxSize.width
            scaledHeight = (maxSize.width / originalAspect).toInt()
        } else {
            scaledWidth = (maxSize.height * originalAspect).toInt()
            scaledHeight = maxSize.height
        }
        Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true)
    }
}

suspend fun ByteSource.toBitmap(maxSize: Size): Bitmap = openStream().toBitmap(maxSize)