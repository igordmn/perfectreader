package com.dmi.perfectreader.settingsui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.dmi.perfectreader.main
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.ViewLoad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.anko.dip
import java.net.URI

class SettingBitmapView(context: Context, size: Int) : ImageView(context), Bindable<String> {
    private val uriHandler = context.main.uriHandler
    private val load = ViewLoad(this)
    private val sizeDip = dip(size)

    init {
        minimumWidth = sizeDip
        minimumHeight = sizeDip
        scaleType = ScaleType.CENTER
        adjustViewBounds = false
    }

    override fun bind(model: String) {
        val path = URI(model)
        setImageBitmap(null)
        load.start {
            val bitmap = withContext(Dispatchers.IO) {
                loadBitmap(path)
            }
            setImageBitmap(bitmap)
        }
    }

    private fun loadBitmap(path: URI): Bitmap = uriHandler.open(path).use {
        val original = BitmapFactory.decodeStream(it)
        val aspectRatio = original.width.toFloat() / original.height
        val scaledWidth: Int
        val scaledHeight: Int
        if (aspectRatio > 1) {
            scaledWidth = sizeDip
            scaledHeight = (sizeDip * aspectRatio).toInt()
        } else {
            scaledWidth = (sizeDip * aspectRatio).toInt()
            scaledHeight = sizeDip
        }
        Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true)
    }
}