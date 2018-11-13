package com.dmi.perfectreader.common

import com.dmi.perfectreader.MainContext
import com.dmi.perfectreader.ui.book.gl.PageAnimationPreviews
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.android.font.androidFontsCache
import com.dmi.util.android.opengl.GLContext
import com.dmi.util.font.Fonts
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.log.Log
import java.net.URI

class Resources(
        private val context: MainContext,
        log: Log = context.log,
        private val settings: Settings = context.settings,
        private val protocols: Protocols = context.protocols,
        private val uriHandler: ProtocolURIHandler = context.uriHandler
) {
    private val fontsCache = androidFontsCache(log)

    val fonts: Fonts
        get() {
            val userDirectory = protocols.fileFor(settings.other.fontsPath)
            return fontsCache[userDirectory]
        }

    val backgrounds: List<URI> get() = uriHandler.children(URI("assets:///resources/backgrounds/"))
    val pageAnimations: List<URI> get() = uriHandler.children(URI("assets:///resources/animations/"))

    fun pageAnimationPreviews(glContext: GLContext) = PageAnimationPreviews(context, glContext)
}