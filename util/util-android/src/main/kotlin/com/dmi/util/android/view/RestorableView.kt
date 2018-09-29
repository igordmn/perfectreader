package com.dmi.util.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.util.set
import org.jetbrains.anko.matchParent

fun View.restorable() = RestorableView(context, this)

/**
 * This view should wrap any of dynamically attached/detached views (root views, loading views, viewpager views, screen views)
 */
@SuppressLint("ViewConstructor")
class RestorableView(context: Context, child: View) : FrameLayout(context) {
    init {
        child(params(matchParent, matchParent), child)

        var id = 1

        fun setIdRecursive(view: View) {
            view.id = ++id
            if (view is ViewGroup && view !is RestorableView) {
                for (i in 0 until view.childCount)
                    setIdRecursive(view.getChildAt(i))
            }
        }

        this.id = ++id
        for (i in 0 until childCount)
            setIdRecursive(getChildAt(i))
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        val superContainer = SparseArray<Parcelable>()
        withId(0) {
            super.dispatchSaveInstanceState(superContainer)
        }
        val bundle = Bundle()
        bundle.putSparseParcelableArray("superContainer", superContainer)
        container[id] = bundle
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        val bundle = container[id] as Bundle
        val superContainer = bundle.getSparseParcelableArray<Parcelable>("superContainer")
        withId(0) {
            super.dispatchRestoreInstanceState(superContainer)
        }
    }

    private inline fun withId(id: Int, action: () -> Unit) {
        val oldId = this.id
        this.id = id
        try {
            action()
        } finally {
            this.id = oldId
        }
    }
}