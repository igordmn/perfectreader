package com.dmi.util.android.view

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup

/**
 * Use this methods only with static views (without dynamically adding children)
 *
 * P.S. dynamically adding children with isSaveFromParentEnabled=false is allowed
 */


fun View.simpleSaveState(): Bundle = withSequentialIds {
    val container = SparseArray<Parcelable>()
    saveHierarchyState(container)
    val state = Bundle()
    state.classLoader = javaClass.classLoader
    state.putSparseParcelableArray("container", container)
    state
}

fun View.simpleRestoreState(state: Bundle) = withSequentialIds {
    state.classLoader = javaClass.classLoader
    val container = state.getSparseParcelableArray<Parcelable>("container")
    restoreHierarchyState(container)
}

fun <T> View.withSequentialIds(action: () -> T): T {
    class OldId(val view: View, val id: Int) {
        fun restore() {
            view.id = id
        }
    }

    val oldIds = ArrayList<OldId>()

    var id = 0

    fun setIdRecursive(v: View) {
        oldIds.add(OldId(v, v.id))
        v.id = ++id
        if (v is ViewGroup) {
            for (i in 0 until v.childCount)
                setIdRecursive(v.getChildAt(i))
        }
    }

    setIdRecursive(this)

    try {
        return action()
    } finally {
        oldIds.forEach(OldId::restore)
    }
}