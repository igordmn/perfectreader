package com.dmi.util.android.view

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View

fun View.saveState(): Bundle {
    val container = SparseArray<Parcelable>()
    saveHierarchyState(container)
    val state = Bundle()
    state.putSparseParcelableArray("container", container)
    return state
}

fun View.restoreState(state: Bundle) {
    val container = state.getSparseParcelableArray<Parcelable>("container")
    restoreHierarchyState(container)
}