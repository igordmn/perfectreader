package com.dmi.perfectreader

import android.content.Context
import com.dmi.perfectreader.book.UserBooks
import com.dmi.perfectreader.book.parse.BookParsers
import com.dmi.perfectreader.common.Databases
import com.dmi.perfectreader.common.Protocols
import com.dmi.perfectreader.common.Resources
import com.dmi.perfectreader.settings.settings
import com.dmi.util.android.io.AssetsURIHandler
import com.dmi.util.android.system.AndroidDisplay
import com.dmi.util.android.system.Permissions
import com.dmi.util.io.FileURIHandler
import com.dmi.util.io.ProtocolURIHandler
import com.dmi.util.log.Log
import com.dmi.util.system.Display
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.displayMetrics

class MainContext(val log: Log, val android: Context) {
    val databases = Databases(log, android)
    val userBooks = UserBooks(databases.default)
    val settings = runBlocking { settings(databases.default) }
    val protocols = Protocols()
    val density = android.displayMetrics.density
    val dip2px = { value: Float -> value * density }
    val uriHandler = ProtocolURIHandler(mapOf(
            "file" to FileURIHandler(),
            "assets" to AssetsURIHandler(android.assets)
    ))
    val display: Display = AndroidDisplay
    val permissions = Permissions(android)
    val resources = Resources(this)
    val bookParsers = BookParsers(log)
}