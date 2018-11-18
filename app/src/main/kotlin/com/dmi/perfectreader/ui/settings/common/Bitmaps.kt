package com.dmi.perfectreader.ui.settings.common

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import com.dmi.perfectreader.main
import com.dmi.util.android.graphics.toBitmap
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.ViewLoad
import com.dmi.util.android.view.container
import com.dmi.util.android.view.into
import com.dmi.util.graphic.Size
import java.net.URI

private class SettingBitmapViewInner(context: Context, private val size: Int) : ImageView(context), Bindable<String> {
    private val uriHandler = context.main.uriHandler
    private val load = ViewLoad(this)

    init {
        scaleType = ScaleType.CENTER
    }

    override fun bind(model: String) {
        setImageBitmap(null)
        if (model.isNotEmpty()) {
            val path = URI(model)
            load.start {
                setImageBitmap(
                        uriHandler
                                .open(path)
                                .buffered()
                                .toBitmap(Size(size, size))
                )
            }
        } else {
            load.cancel()
        }
    }
}

class SettingBitmapView(context: Context, size: Int) : FrameLayout(context), Bindable<String> {
    private val inner = SettingBitmapViewInner(context, size) into container(size, size, gravity = Gravity.CENTER)

    override fun bind(model: String) {
        inner.bind(model)
    }
}