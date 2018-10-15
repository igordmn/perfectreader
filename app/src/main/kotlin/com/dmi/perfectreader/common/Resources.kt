package com.dmi.perfectreader.common

import com.dmi.perfectreader.Main
import com.dmi.perfectreader.book.gl.PageAnimationPreviews
import com.dmi.perfectreader.settings.Settings
import com.dmi.util.android.font.androidFontsCache
import com.dmi.util.android.opengl.GLContext
import com.dmi.util.font.Fonts
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.log.Log
import java.net.URI

class Resources(
        private val main: Main,
        log: Log = main.log,
        private val settings: Settings = main.settings,
        private val protocols: Protocols = main.protocols,
        private val uriHandler: ProtocolURIHandler = main.uriHandler
) {
    private val fontsCache = androidFontsCache(log)

    val fonts: Fonts
        get() {
            val userDirectory = protocols.fileFor(settings.system.fontsPath)
            return fontsCache[userDirectory]
        }

    val backgrounds: List<URI> get() = uriHandler.children(URI("assets:///resources/backgrounds/"))
    val pageAnimations: List<URI> get() = uriHandler.children(URI("assets:///resources/animations/"))

    fun pageAnimationPreviews(glContext: GLContext) = PageAnimationPreviews(main, glContext)
}