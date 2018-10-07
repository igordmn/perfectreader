package com.dmi.util.android.view

import android.content.Context

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class GridAutoFitLayoutManager(context: Context, private val columnWidth: Int) : GridLayoutManager(context, 1) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (width > 0) {
            val totalSpace = width - paddingRight - paddingLeft
            spanCount = max(1, totalSpace / columnWidth)
        }
        super.onLayoutChildren(recycler, state)
    }
}