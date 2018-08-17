package com.dmi.util.android.view

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter(private vararg val titlesWithCreateViews: Pair<CharSequence, () -> View>) : PagerAdapter() {
    override fun isViewFromObject(view: View, obj: Any) = view === obj
    override fun getCount() = titlesWithCreateViews.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return titlesWithCreateViews[position].second().also {
            container.addView(it)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun getPageTitle(position: Int) = titlesWithCreateViews[position].first
}