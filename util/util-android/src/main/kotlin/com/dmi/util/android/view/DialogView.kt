package com.dmi.util.android.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.widget.FrameLayout
import androidx.core.util.set

@SuppressLint("ViewConstructor")
class DialogView(context: Context, private val createDialog: () -> Dialog) : View(context) {
    private var dialog: Dialog? = null
    private var restoredState: Bundle? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val dialog = createDialog()
        val restoredState = restoredState
        dialog.show()
        if (restoredState != null) {
            dialog.onRestoreInstanceState(restoredState)
            this.restoredState = null
        }
        this.dialog = dialog
    }

    override fun onDetachedFromWindow() {
        dialog!!.dismiss()
        dialog = null
        super.onDetachedFromWindow()
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable?>) {
        if (id != FrameLayout.NO_ID) {
            container[id] = dialog?.onSaveInstanceState()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable?>) {
        if (id != FrameLayout.NO_ID) {
            restoredState = container[id] as Bundle?
        }
    }
}