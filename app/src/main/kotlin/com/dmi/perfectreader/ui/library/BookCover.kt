package com.dmi.perfectreader.ui.library

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.dmi.perfectreader.R
import com.dmi.util.android.graphics.toBitmap
import com.dmi.util.android.view.*
import com.dmi.util.graphic.Size
import com.google.common.io.ByteSource
import org.jetbrains.anko.image
import org.jetbrains.anko.padding
import org.jetbrains.anko.textColor
import kotlin.random.Random

class BookCover(context: Context, private val imageSize: Size) : FrameLayout(context), Bindable<BookCover.Content> {
    private val saturation1 = 0.40F
    private val saturation2 = 0.60F
    private val value1 = 0.40F
    private val value2 = 0.60F

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
                image.image = randomGradient(model.name)
                text.text = model.name
            }
        }
    }

    private fun randomGradient(name: String) : GradientDrawable {
        val hash = name.map { it.toInt() }.sum() % 100
        val random = Random(52364365L)
        repeat(hash) {
            random.nextInt()
        }
        val hue = random.nextInt(360).toFloat()
        val color1: Int = Color.HSVToColor(floatArrayOf(hue, saturation1, value1))
        val color2: Int = Color.HSVToColor(floatArrayOf(hue, saturation2, value2))
        return GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(color1, color2))
    }

    class Content(val image: ByteSource?, val name: String)
}