package com.dmi.util.android.screen

import android.view.View
import android.widget.FrameLayout
import com.dmi.util.android.view.ViewBuild
import com.dmi.util.android.view.bindChild
import com.dmi.util.android.view.child
import com.dmi.util.android.view.params
import org.jetbrains.anko.matchParent
import kotlin.reflect.KProperty0

fun <M : Any> View.withPopup(
        property: KProperty0<M?>,
        createView: ViewBuild.(model: M) -> View
) = FrameLayout(context).apply {
    child(params(matchParent, matchParent), this@withPopup)
    bindChild(params(matchParent, matchParent), property, createView)
}