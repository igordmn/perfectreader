package com.dmi.perfectreader.settingschange.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.util.android.view.attr
import com.dmi.util.android.view.child
import com.dmi.util.android.view.params
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.onClick
import org.jetbrains.anko.wrapContent
import kotlin.math.max
import kotlin.reflect.KMutableProperty0

class SettingScrollView<T, V : View>(
        context: Context,
        private val property: KMutableProperty0<T>,
        private val items: List<T>,
        private val view: () -> V,
        private val bind: V.(item: T) -> Unit
) : RecyclerView(context) {
    private var activatedPosition = max(0, items.indexOf(property.get()))

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        setHasFixedSize(true)
        adapter = object : Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return object : ViewHolder(ItemView(view())) {}
            }

            override fun getItemCount() = items.size

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                @Suppress("UNCHECKED_CAST")
                val itemView = holder.itemView as SettingScrollView<T, V>.ItemView
                itemView.bind(position)
            }
        }
        scrollToPosition(activatedPosition)
    }

    private val allItemViews = ArrayList<ItemView>()

    private inner class ItemView(val original: V) : FrameLayout(context) {
        var position: Int = 0

        init {
            allItemViews.add(this)
            child(original, params(wrapContent, wrapContent))
            isClickable = true
            isFocusable = true
            backgroundResource = attr(android.R.attr.activatedBackgroundIndicator).resourceId
            onClick { activate() }
        }

        fun bind(position: Int) {
            this.position = position
            isActivated = activatedPosition == position
            original.bind(items[position])
        }

        private fun activate() {
            allItemViews.forEach {
                it.isActivated = it == this
            }
            activatedPosition = position
            property.set(items[position])
        }
    }
}