package com.dmi.perfectreader.settingsui.common

import android.content.Context
import android.widget.ImageView
import com.dmi.perfectreader.main
import com.dmi.util.android.graphics.toBitmap
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.ViewLoad
import com.dmi.util.graphic.Size
import org.jetbrains.anko.dip
import java.net.URI

class SettingBitmapView(context: Context, size: Int) : ImageView(context), Bindable<String> {
    private val uriHandler = context.main.uriHandler
    private val load = ViewLoad(this)
    private val size = dip(size)

    init {
        minimumWidth = this.size
        minimumHeight = this.size
        scaleType = ScaleType.CENTER
        adjustViewBounds = false
    }

    override fun bind(model: String) {
        val path = URI(model)
        setImageBitmap(null)
        load.start {
            setImageBitmap(
                    uriHandler
                            .open(path)
                            .buffered()
                            .toBitmap(Size(size, size))
            )
        }
    }
}