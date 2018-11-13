package com.dmi.perfectreader.settingsui.common

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.dmi.perfectreader.R
import com.dmi.util.android.view.Bindable
import com.dmi.util.android.view.BindableViewAdapter
import com.dmi.util.android.view.child
import com.dmi.util.android.view.params
import com.dmi.util.scope.observable
import org.jetbrains.anko.*

// todo tune ripple effect
class SettingSelectListViewAdapter<T, V>(
        private val context: Context,
        private val getItems: () -> List<T>,
        private val createItemView: (Context) -> V,
        private val onItemClick: (T) -> Unit = {}
) : BindableViewAdapter<SettingSelectListViewAdapter<*, *>.ItemView>() where V : View, V : Bindable<T> {
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
            layoutParams = params(matchParent, wrapContent)
            child(params(matchParent, wrapContent), original)
            isClickable = true
            isFocusable = true
            backgroundResource = R.drawable.settings_list_item_background
            onLongClick { switchSelection(); true }
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
                onItemClick(getItems()[position])
            }
        }
    }
}