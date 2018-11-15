package com.dmi.perfectreader.ui.settings.common.list

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmi.perfectreader.R
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.BindableViewAdapter
import com.dmi.util.android.view.container
import com.dmi.util.android.view.into
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.wrapContent
import kotlin.math.max
import kotlin.reflect.KMutableProperty0

class SettingSingleChoiceListView<T, V>(
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
        val layoutManager = LinearLayoutManager(context)
        this.layoutManager = layoutManager
        adapter = object : BindableViewAdapter<ItemView>() {
            override fun getItemCount() = items.size
            override fun view(viewType: Int) = ItemView(createItemView(context))
        }
        layoutManager.scrollToPositionWithOffset(activatedPosition, dip(80))
    }

    private inner class ItemView(val original: V) : FrameLayout(context), Bindable<Int> {
        var position: Int = 0

        init {
            allItemViews.add(this)
            layoutParams = FrameLayout.LayoutParams(matchParent, wrapContent)
            original into container(matchParent, wrapContent)
            isClickable = true
            isFocusable = true
            backgroundResource = R.drawable.settings_list_item_background
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