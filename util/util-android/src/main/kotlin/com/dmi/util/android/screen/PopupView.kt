package com.dmi.util.android.screen

import android.view.View
import android.widget.FrameLayout
import com.dmi.util.android.view.ViewBuild
import com.dmi.util.android.view.bindChild
import com.dmi.util.android.view.container
import com.dmi.util.android.view.into
import org.jetbrains.anko.matchParent
import kotlin.reflect.KProperty0

fun <M : Any> View.withPopup(
        viewBuild: ViewBuild,
        property: KProperty0<M?>,
        createView: ViewBuild.(model: M) -> View
) = FrameLayout(context).apply {
    this@withPopup into container(matchParent, matchParent)
    viewBuild.bindChild(container(matchParent, matchParent), property, createView).apply {
        id = viewBuild.generateId()
    }
}