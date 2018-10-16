package com.dmi.util.android.view

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dmi.util.graphic.Size

val View.size: Size get() = Size(width, height)

operator fun View.contains(event: MotionEvent): Boolean {
    val rect = Rect()
    getGlobalVisibleRect(rect)
    return rect.contains(event.rawX.toInt(), event.rawY.toInt())
}

interface Bindable<M> {
    fun bind(model: M)
}

abstract class BindableViewAdapter<V> : RecyclerView.Adapter<RecyclerView.ViewHolder>() where V : View, V : Bindable<Int> {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(view()) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        @Suppress("UNCHECKED_CAST")
        (holder.itemView as V).bind(position)
    }

    protected abstract fun view(): V
}