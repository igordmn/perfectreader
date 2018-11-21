package com.dmi.perfectreader.ui.action

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import com.google.android.material.card.MaterialCardView
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textColor
import org.jetbrains.anko.wrapContent
import java.util.*

fun ViewBuild.performingActionView(model: PerformingAction, old: PerformingActionView?): PerformingActionView {
    val view = old ?: PerformingActionView(context)
    view.set(model)
    return view
}

class PerformingActionView(context: Context) : FrameLayout(context) {
    private val name: TextView
    private val value: TextView

    init {
        FrameLayout(context).apply {
            MaterialCardView(context).apply {
                setBackgroundColor(color(R.color.secondary))
                elevation = dipFloat(3F)
                useCompatPadding = true

                LinearLayoutCompat(context).apply {
                    orientation = LinearLayoutCompat.VERTICAL
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(dip(12), dip(4), dip(12), dip(4))

                    name = AppCompatTextView(context).apply {
                        isAllCaps = true
                        textColor = color(R.color.onSecondary)
                        textSize = spFloat(16F)
                        gravity = Gravity.CENTER_HORIZONTAL
                    } into container(wrapContent, wrapContent)

                    value = AppCompatTextView(context).apply {
                        isAllCaps = true
                        textColor = color(R.color.onSecondary)
                        textSize = spFloat(24F)
                    } into container(wrapContent, wrapContent)
                } into container(wrapContent, wrapContent)
            } into container(wrapContent, wrapContent, gravity = Gravity.CENTER_HORIZONTAL)
        } into container(matchParent, matchParent)
    }

    fun set(action: PerformingAction) {
        name.text = numberSettingActionName(context, action.id)
        value.text = formatFloatValue(action.value)
    }

    private fun formatFloatValue(value: Float) = String.format(Locale.US, "%.2f", value)
}