package com.dmi.perfectreader.settingsui.common

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.BindableViewAdapter
import com.dmi.util.android.view.child
import com.dmi.util.android.view.params
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.onClick
import org.jetbrains.anko.wrapContent
import kotlin.math.max
import kotlin.reflect.KMutableProperty0

class SettingListView<T, V>(
        context: Context,
        private val property: KMutableProperty0<T>,
        private val items: List<T>,
        private val createItemView: (Context) -> V,
        private val onItemClick: () -> Unit = {},
        private val onItemSecondClick: () -> Unit = {}
) : RecyclerView(context, null, R.attr.verticalRecyclerViewStyle) where V : View, V : Bindable<T> {
    private var activatedPosition = max(0, items.indexOf(property.get()))
    private val allItemViews = ArrayList<ItemView>()

    init {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        adapter = object : BindableViewAdapter<ItemView>() {
            override fun getItemCount() = items.size
            override fun view() = ItemView(createItemView(context))
        }
        scrollToPosition(activatedPosition)
    }

    private inner class ItemView(val original: V) : FrameLayout(context), Bindable<Int> {
        var position: Int = 0

        init {
            allItemViews.add(this)
            layoutParams = params(matchParent, wrapContent)
            child(params(matchParent, wrapContent), original)
            isClickable = true
            isFocusable = true
            backgroundResource = R.drawable.list_item_background
            onClick { activate() }
        }

        override fun bind(model: Int) {
            position = model
            isActivated = activatedPosition == position
            original.bind(items[position])
        }

        private fun activate() {
            allItemViews.forEach {
                it.isActivated = it == this
            }
            val isChanged = activatedPosition != position
            activatedPosition = position

            if (isChanged)
                property.set(items[position])

            onItemClick()
            if (!isChanged)
                onItemSecondClick()
        }
    }
}