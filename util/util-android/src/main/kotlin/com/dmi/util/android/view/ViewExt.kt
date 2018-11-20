package com.dmi.util.android.view

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dmi.util.graphic.Size
import org.jetbrains.anko.sdk27.coroutines.onTouch

val View.size: Size get() = Size(width, height)

fun View.ownsTouchEvents() = onTouch(returnValue = true) { _, _ ->  }

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
        return object : RecyclerView.ViewHolder(view(viewType)) {}
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as V).bind(position)
    }

    protected abstract fun view(viewType: Int): V
}