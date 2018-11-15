package com.dmi.perfectreader.ui.selection

import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import com.dmi.perfectreader.R
import com.dmi.util.android.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

fun ViewBuild.selectionView(model: Selection) = FrameLayoutExt {
    fun updateActions(container: ViewGroup, actions: View) {
        actions.visibility = if (model.actionsIsVisible) View.VISIBLE else View.GONE
        val position = model.actionsPosition(container.size, actions.size)
        val layoutParams = actions.layoutParams as FrameLayout.LayoutParams
        layoutParams.leftMargin = position.x
        layoutParams.topMargin = position.y
        actions.layoutParams = layoutParams
    }

    fun actions() = MaterialCardView {
        cardElevation = dipFloat(6F)
        useCompatPadding = true

        ScrollView {
            backgroundColor = color(R.color.background)

            HorizontalLayout {
                fun item(@DrawableRes icon: Int, @StringRes str: Int, action: () -> Unit) {
                    AppCompatImageButton {
                        backgroundResource = attr(R.attr.selectableItemBackground).resourceId
                        contentDescription = string(str)
                        image = drawable(icon, color(R.color.onBackground))
                        TooltipCompat.setTooltipText(this, contentDescription)
                        onClick { action() }
                    } into container(dip(48), dip(48))
                }

                item(R.drawable.ic_content_copy, R.string.selectionCopy, model::copySelectedText)
                item(R.drawable.ic_translate, R.string.selectionTranslate, model::translateSelectedText)
                item(R.drawable.ic_search, R.string.selectionSearchBook, model::searchBookSelectedText)
                item(R.drawable.ic_search_web, R.string.selectionSearchWeb, model::searchWebSelectedText)
                item(R.drawable.ic_search_wiki, R.string.selectionSearchWiki, model::searchWikiSelectedText)
            } into container(wrapContent, wrapContent)
        } into container(wrapContent, wrapContent)
    }

    FrameLayout {
        val handles = HandlesView(context, model) into container(wrapContent, wrapContent)
        autorun {
            handles.set(model.handles)
        }
    } into container(matchParent, matchParent)

    FrameLayout {
        layoutTransition = fadeTransition(300)
        val actions = actions() into container(wrapContent, wrapContent)
        onSizeChange { _, _ ->
            updateActions(this, actions)
        }
        autorun {
            updateActions(this, actions)
        }
    } into container(matchParent, matchParent)

    onInterceptKeyDown(KeyEvent.KEYCODE_BACK) { model.deselect(); true }
}