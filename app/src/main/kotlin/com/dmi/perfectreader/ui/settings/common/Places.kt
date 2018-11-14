package com.dmi.perfectreader.ui.settings.common

import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.dmi.perfectreader.ui.settings.SettingsUI
import com.dmi.util.android.view.DialogView
import com.dmi.util.android.view.Places
import com.dmi.util.android.view.string
import kotlin.reflect.KMutableProperty0

fun <T> Places.singleChoice(
        model: SettingsUI,
        property: KMutableProperty0<T>,
        values: Array<T>,
        format: (T) -> String,
        @StringRes titleRes: Int
) = place {
    val names: Array<String> = values.map(format).toTypedArray()

    DialogView(context) {
        val current = values.indexOf(property.get())
        AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setSingleChoiceItems(names, current) { dialog, which ->
                    property.set(values[which])
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    model.popup = null
                }
                .create()
    }
}

fun Places.multiChoice(
        model: SettingsUI,
        property: KMutableProperty0<BooleanArray>,
        namesRes: Array<Int>,
        @StringRes titleRes: Int
)  = place {
    val names = namesRes.map { context.string(it) }.toTypedArray()

    DialogView(context) {
        val checked = property.get()
        AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    property.set(checked)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .setMultiChoiceItems(names, checked) { _, which, isChecked ->
                    checked[which] = isChecked
                }
                .setOnDismissListener {
                    model.popup = null
                }
                .create()
    }
}