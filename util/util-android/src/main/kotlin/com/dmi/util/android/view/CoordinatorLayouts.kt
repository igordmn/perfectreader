package com.dmi.util.android.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView

fun removeElevationOnScroll(recyclerView: RecyclerView, toolbar: View) {
    val elevation = toolbar.elevation
    toolbar.elevation = 0F
    recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            toolbar.elevation =  if(!recyclerView.canScrollVertically(-1)) 0F else elevation
        }
    })
}