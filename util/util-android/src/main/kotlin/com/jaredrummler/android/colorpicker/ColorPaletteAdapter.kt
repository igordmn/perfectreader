package com.jaredrummler.android.colorpicker

import android.widget.BaseAdapter

fun colorPaletteAdapter(
        colors: IntArray,
        selectedPosition: Int,
        @ColorShape colorShape: Int,
        onColorSelected: (color: Int) -> Unit
): BaseAdapter = ColorPaletteAdapter(
        ColorPaletteAdapter.OnColorSelectedListener { color -> onColorSelected(color) },
        colors, selectedPosition, colorShape
)