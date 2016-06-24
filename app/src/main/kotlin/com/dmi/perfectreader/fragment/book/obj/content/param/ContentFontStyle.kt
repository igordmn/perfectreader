package com.dmi.perfectreader.fragment.book.obj.content.param

import com.dmi.perfectreader.fragment.book.obj.common.SelectionConfig
import com.dmi.perfectreader.fragment.book.obj.common.TextRenderConfig
import com.dmi.util.graphic.Color
import java.io.Serializable

class ContentFontStyle(
        val size: Float?,
        val color: Color?
) : Serializable

class ComputedFontStyle(
        val size: Float,
        val color: Color,
        val renderConfig: TextRenderConfig,
        val selectionConfig: SelectionConfig
)