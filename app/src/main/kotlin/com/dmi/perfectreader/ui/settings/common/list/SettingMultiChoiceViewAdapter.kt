package com.dmi.perfectreader.ui.settings.common.list

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.dmi.perfectreader.R
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.BindableViewAdapter
import com.dmi.util.android.view.container
import com.dmi.util.android.view.into
import com.dmi.util.scope.observable
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.sdk27.coroutines.onLongClick
import org.jetbrains.anko.wrapContent

// todo tune ripple effect
class SettingMultiChoiceViewAdapter<T, V>(
        private val context: Context,
        private val getItems: () -> List<T>,
        private val createItemView: (Context) -> V,
        private val onItemClick: (position: Int, T) -> Unit = { _, _ -> }
) : BindableViewAdapter<SettingMultiChoiceViewAdapter<*, *>.ItemView>() where V : View, V : Bindable<T> {
    private val allItemViews = ArrayList<ItemView>()
    var selectedPositions: Set<Int> by observable(emptySet())
        private set

    override fun getItemCount() = getItems().size
    override fun view(viewType: Int) = ItemView(createItemView(context))

    fun selectAll() {
        selectedPositions = getItems().indices.toSet()
        for (item in allItemViews) {
            item.isActivated = true
        }
    }

    fun deselect() {
        selectedPositions = emptySet()
        for (item in allItemViews) {
            item.isActivated = false
        }
    }

    inner class ItemView(val original: V) : FrameLayout(context), Bindable<Int> {
        private var position: Int = 0

        init {
            allItemViews.add(this)
            layoutParams = FrameLayout.LayoutParams(matchParent, wrapContent)
            original into container(matchParent, wrapContent)
            isClickable = true
            isFocusable = true
            backgroundResource = R.drawable.settings_list_item_background
            onLongClick(returnValue = true) { switchSelection() }
            onClick { click() }
        }

        override fun bind(model: Int) {
            position = model
            isActivated = selectedPositions.contains(position)
            original.bind(getItems()[position])
        }

        private fun switchSelection() {
            val set = LinkedHashSet(selectedPositions)
            if (set.contains(position)) {
                set.remove(position)
            } else {
                set.add(position)
            }
            isActivated = set.contains(position)
            selectedPositions = set
        }

        private fun click() {
            if (selectedPositions.isNotEmpty()) {
                switchSelection()
            } else {
                onItemClick(position, getItems()[position])
            }
        }
    }
}