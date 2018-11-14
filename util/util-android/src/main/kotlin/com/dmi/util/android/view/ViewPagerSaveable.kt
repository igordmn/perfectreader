package com.dmi.util.android.view

import android.content.Context
import android.os.Parcelable
import android.util.SparseArray
import androidx.viewpager.widget.ViewPager

// todo it is really needed now?
class ViewPagerSaveable(context: Context) : ViewPager(context) {
    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        dispatchThawSelfOnly(container)
    }
}