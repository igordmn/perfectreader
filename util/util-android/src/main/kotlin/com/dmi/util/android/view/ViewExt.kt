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

class HeaderViewAdapter<VH : RecyclerView.ViewHolder>(
        private val original: RecyclerView.Adapter<VH>,
        val headerViewType: Int = 346847457
) : RecyclerView.Adapter<VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = original.onCreateViewHolder(parent, viewType)
    override fun onBindViewHolder(holder: VH, position: Int) = original.onBindViewHolder(holder, position)
    override fun getItemViewType(position: Int): Int = 0 //if (position == 0) headerViewType else original.getItemViewType(position)
    override fun getItemId(position: Int): Long = original.getItemId(position)
    override fun getItemCount(): Int = 1 + original.itemCount
    override fun onViewRecycled(holder: VH) = original.onViewRecycled(holder)
    override fun onFailedToRecycleView(holder: VH): Boolean = original.onFailedToRecycleView(holder)
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) = original.onAttachedToRecyclerView(recyclerView)
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) = original.onDetachedFromRecyclerView(recyclerView)
    override fun onViewAttachedToWindow(holder: VH) = original.onViewAttachedToWindow(holder)
    override fun onViewDetachedFromWindow(holder: VH) = original.onViewDetachedFromWindow(holder)
}