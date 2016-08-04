package com.dmi.perfectreader.fragment.book.render.obj

import android.graphics.Bitmap
import com.dmi.perfectreader.fragment.book.layout.obj.LayoutImage

class RenderImage(x: Float, y: Float, override val layoutObj: LayoutImage, val bitmap: Bitmap) : RenderObject(x, y)