package com.dmi.perfectreader.library

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.dmi.perfectreader.R
import com.dmi.util.android.graphics.toBitmap
import com.dmi.util.android.view.*
import com.dmi.util.graphic.Size
import com.google.common.io.ByteSource
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColor

class BookCover(context: Context, private val imageSize: Size) : FrameLayout(context), Bindable<BookCover.Content> {
    private val image = child(params(imageSize.width, imageSize.height), ImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER
    })
    private val text = child(params(imageSize.width, imageSize.height), TextView(context).apply {
        padding = imageSize.width / 12
        gravity = Gravity.CENTER
        textColor = color(R.color.onSecondary)
        textSize = imageSize.width / 8F
    })

    private val load = ViewLoad(this)

    override fun bind(model: Content) {
        image.setImageBitmap(null)
        text.text = null
        load.start {
            val bitmap = model.image?.toBitmap(imageSize)
            if (bitmap != null) {
                image.setImageBitmap(bitmap)
            } else {
                image.imageResource = R.drawable.library_cover
                text.text = model.name
            }
        }
    }

    class Content(val image: ByteSource?, val name: String)
}