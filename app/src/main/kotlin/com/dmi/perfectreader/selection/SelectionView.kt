package com.dmi.perfectreader.selection

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.TooltipCompat
import androidx.cardview.widget.CardView
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import org.jetbrains.anko.*

fun Context.selectionView(model: Selection) = view(::FrameLayoutExt) {
    fun updateActions(container: ViewGroup, actions: View) {
        actions.visibility = if (model.actionsIsVisible) View.VISIBLE else View.GONE
        val position = model.actionsPosition(container.size, actions.size)
        val layoutParams = actions.layoutParams as FrameLayout.LayoutParams
        layoutParams.leftMargin = position.x
        layoutParams.topMargin = position.y
        actions.layoutParams = layoutParams
    }

    fun actions() = view(::CardView) {
        cardElevation = dipFloat(8F)
        useCompatPadding = true

        child(::ScrollView, params(wrapContent, wrapContent)) {
            backgroundColor = color(R.color.gray100)

            child(::LinearLayoutCompat, params(wrapContent, wrapContent)) {
                orientation = LinearLayoutCompat.HORIZONTAL

                child(::AppCompatImageButton, params(dip(48), dip(48))) {
                    backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                    contentDescription = string(R.string.selectionCopyText)
                    image = drawable(R.drawable.ic_content_copy, color(R.color.icon_dark))
                    onClick { model.copySelectedText() }
                    TooltipCompat.setTooltipText(this, contentDescription)
                }

                child(::AppCompatImageButton, params(dip(48), dip(48))) {
                    backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                    contentDescription = string(R.string.selectionTranslateText)
                    image = drawable(R.drawable.ic_translate, color(R.color.icon_dark))
                    onClick { model.translateSelectedText() }
                    TooltipCompat.setTooltipText(this, contentDescription)
                }
            }
        }
    }

    child(::FrameLayout, params(matchParent, matchParent)) {
        val handles = child(HandlesView(context, model), params(wrapContent, wrapContent))
        autorun {
            handles.set(model.handles)
        }
    }

    child(::FrameLayout, params(matchParent, matchParent)) {
        layoutTransition = fadeTransition(200)
        val actions = child(actions(), params(wrapContent, wrapContent))
        onSizeChange { _, _ ->
            updateActions(this, actions)
        }
        autorun {
            updateActions(this, actions)
        }
    }

    onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.deselect(); true }
}