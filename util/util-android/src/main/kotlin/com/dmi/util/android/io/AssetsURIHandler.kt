package com.dmi.util.android.io

import android.content.res.AssetManager
import com.dmi.util.io.URIHandler
import java.io.InputStream
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder

// todo maybe remove manually all prefixes '/' from all paths in code?
class AssetsURIHandler(private val assetManager: AssetManager) : URIHandler {
    override fun open(uri: URI): InputStream = assetManager.open(URLDecoder.decode(uri.path.removePrefix("/"), "UTF-8"))

    override fun children(uri: URI): List<URI> = assetManager.list(uri.path.removePrefix("/").removeSuffix("/"))!!.map {
        uri.resolve(URLEncoder.encode(it, "UTF-8"))
    }
}