package com.dmi.util.android.view

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter(vararg titlesWithCreateViews: Pair<CharSequence, () -> View>) : PagerAdapter() {
    private val items = titlesWithCreateViews.map { Item(it.first, it.second) }

    private val savedStates = Array<Bundle?>(titlesWithCreateViews.size) { null }
    private val instantiated = Array<View?>(titlesWithCreateViews.size) { null }

    override fun isViewFromObject(view: View, obj: Any) = view === obj
    override fun getCount() = items.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = items[position].createView()
        val state = savedStates[position]
        if (state != null)
            view.restoreState(state)
        container.addView(view)
        savedStates[position] = null
        instantiated[position] = view
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        view as View
        val state = view.saveState()
        container.removeView(view)
        savedStates[position] = state
        instantiated[position] = null
    }

    override fun getPageTitle(position: Int) = items[position].title

    override fun saveState(): Parcelable? {
        val state = Bundle()
        val array = ArrayList<Bundle?>()
        for (i in items.indices) {
            var itemState = savedStates[i]
            val view = instantiated[i]
            if (view != null) {
                require(itemState == null)
                itemState = view.saveState()
            }
            array.add(itemState)
        }
        state.putParcelableArray("items", array.toTypedArray())
        return state
    }

    @Suppress("UNCHECKED_CAST")
    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        if (state != null) {
            state as Bundle
            state.classLoader = loader
            val array = state.getParcelableArray("items")

            if (array != null && items.size == array.size) {
                restoreState(array)
            }
        }
    }

    private fun restoreState(array: Array<out Parcelable?>) {
        for (i in items.indices) {
            val itemState = array[i] as Bundle?
            val instantiated = instantiated[i]
            if (instantiated != null) {
                if (itemState != null)
                    instantiated.restoreState(itemState)
            } else {
                savedStates[i] = itemState
            }
        }
    }

    private class Item(val title: CharSequence, val createView: () -> View)
}