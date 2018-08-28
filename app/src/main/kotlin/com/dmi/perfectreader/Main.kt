package com.dmi.perfectreader

import android.app.Activity
import android.content.Context
import com.dmi.perfectreader.common.Databases
import com.dmi.perfectreader.common.Permissions
import com.dmi.perfectreader.common.Protocols
import com.dmi.perfectreader.common.UserData
import com.dmi.perfectreader.settings.settings
import com.dmi.util.android.font.androidFontsCache
import com.dmi.util.android.io.AssetsURIHandler
import com.dmi.util.android.system.AndroidDisplay
import com.dmi.util.font.Fonts
import com.dmi.util.io.FileURIHandler
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.log.Log
import com.dmi.util.system.Display
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.displayMetrics

class Main(val log: Log, val applicationContext: Context) {
    val databases = Databases(log, applicationContext)
    val userData = UserData(databases.default)
    val settings = runBlocking { settings(databases.default) }
    val protocols = Protocols()
    val density = applicationContext.displayMetrics.density
    val dip2px = { value: Float -> value * density }
    val uriHandler = ProtocolURIHandler(mapOf(
            "file" to FileURIHandler(),
            "assets" to AssetsURIHandler(applicationContext.assets)
    ))
    val display: Display = AndroidDisplay
    val permissions = Permissions(this)
    var currentActivity: Activity? = null

    private val fontsCache = androidFontsCache(log)
    val fonts: Fonts get() {
        val userDirectory = protocols.fileFor(settings.system.fontsPath)
        return fontsCache[userDirectory]
    }
}