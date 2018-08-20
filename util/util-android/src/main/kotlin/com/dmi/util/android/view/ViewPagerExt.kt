package com.dmi.util.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class ViewPagerExt : ViewPager {
    var isScrollEnabled = true

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent) = isScrollEnabled && super.onTouchEvent(event)
    override fun onInterceptTouchEvent(event: MotionEvent) = isScrollEnabled && super.onInterceptTouchEvent(event)
}

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