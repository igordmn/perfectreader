package com.dmi.perfectreader.selection

import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.TooltipCompat
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import com.google.android.material.card.MaterialCardView
import org.jetbrains.anko.*

fun ViewBuild.selectionView(model: Selection) = FrameLayoutExt(context).apply {
    fun updateActions(container: ViewGroup, actions: View) {
        actions.visibility = if (model.actionsIsVisible) View.VISIBLE else View.GONE
        val position = model.actionsPosition(container.size, actions.size)
        val layoutParams = actions.layoutParams as FrameLayout.LayoutParams
        layoutParams.leftMargin = position.x
        layoutParams.topMargin = position.y
        actions.layoutParams = layoutParams
    }

    fun actions() = MaterialCardView(context).apply {
        cardElevation = dipFloat(6F)
        useCompatPadding = true

        child(params(wrapContent, wrapContent), ScrollView(context).apply {
            backgroundColor = color(R.color.background)

            child(params(wrapContent, wrapContent), LinearLayoutCompat(context).apply {
                orientation = LinearLayoutCompat.HORIZONTAL

                fun item(@DrawableRes icon: Int, @StringRes str: Int, action: () -> Unit) {
                    child(params(dip(48), dip(48)), AppCompatImageButton(context).apply {
                        backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                        contentDescription = string(str)
                        image = drawable(icon, color(R.color.onBackground))
                        TooltipCompat.setTooltipText(this, contentDescription)
                        onClick { action() }
                    })
                }

                item(R.drawable.ic_content_copy, R.string.selectionCopy, model::copySelectedText)
                item(R.drawable.ic_translate, R.string.selectionTranslate, model::translateSelectedText)
                item(R.drawable.ic_search, R.string.selectionSearchBook, model::searchBookSelectedText)
                item(R.drawable.ic_search_web, R.string.selectionSearchWeb, model::searchWebSelectedText)
                item(R.drawable.ic_search_wiki, R.string.selectionSearchWiki, model::searchWikiSelectedText)
            })
        })
    }

    child(params(matchParent, matchParent), FrameLayout(context).apply {
        val handles = child(params(wrapContent, wrapContent), HandlesView(context, model))
        autorun {
            handles.set(model.handles)
        }
    })

    child(params(matchParent, matchParent), FrameLayout(context).apply {
        layoutTransition = fadeTransition(300)
        val actions = child(params(wrapContent, wrapContent), actions())
        onSizeChange { _, _ ->
            updateActions(this, actions)
        }
        autorun {
            updateActions(this, actions)
        }
    })

    onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.deselect(); true }
}