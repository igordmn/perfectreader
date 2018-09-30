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
import androidx.core.view.children
import org.jetbrains.anko.matchParent

fun View.restorable() = RestorableView(context, this)

/**
 * This view should wrap any of dynamically attached/detached views (root views, loading views, viewpager views, screen views)
 */
@SuppressLint("ViewConstructor")
class RestorableView(context: Context, view: View) : FrameLayout(context) {
    init {
        child(params(matchParent, matchParent), view)

        var id = 0

        fun setIdRecursive(v: View) {
            v.id = ++id
            if (v is ViewGroup && v !is RestorableView) {
                for (i in 0 until v.childCount)
                    setIdRecursive(v.getChildAt(i))
            }
        }

        this.id = ++id

        for (child in children) {
            setIdRecursive(child)
        }
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        val childContainer = SparseArray<Parcelable>()
        for (child in children) {
            if (child.isSaveFromParentEnabled)
                child.saveHierarchyState(childContainer)
        }

        val bundle = Bundle()
        bundle.putSparseParcelableArray("childContainer", childContainer)
        container[id] = bundle
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        val bundle = container[id] as Bundle
        val childContainer = bundle.getSparseParcelableArray<Parcelable>("childContainer")
        for (child in children) {
            if (child.isSaveFromParentEnabled)
                child.restoreHierarchyState(childContainer)
        }
    }
}