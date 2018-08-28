package com.dmi.perfectreader.settingschange.common

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.attr
import com.dmi.util.android.view.child
import com.dmi.util.android.view.params
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.onClick
import org.jetbrains.anko.wrapContent
import kotlin.math.max

class SettingListView<T, V>(
        context: Context,
        value: T,
        private val items: List<T>,
        private val createItemView: (Context) -> V
) : RecyclerView(context) where V : View, V : Bindable<T> {
    private var activatedPosition = max(0, items.indexOf(value))

    private lateinit var onActivate: ((T, changed: Boolean) -> Unit)

    init {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        adapter = object : Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return object : ViewHolder(ItemView(createItemView(context))) {}
            }

            override fun getItemCount() = items.size

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                @Suppress("UNCHECKED_CAST")
                val itemView = holder.itemView as SettingListView<T, V>.ItemView
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
            layoutParams = params(matchParent, wrapContent)
            child(original, params(matchParent, wrapContent))
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
            val isChanged = activatedPosition != position
            activatedPosition = position
            onActivate(items[position], isChanged)
        }
    }
}