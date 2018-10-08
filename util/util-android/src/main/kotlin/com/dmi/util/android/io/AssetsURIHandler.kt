package com.dmi.util.android.io

import android.content.res.AssetManager
import com.dmi.util.io.URIHandler
import java.io.InputStream
import java.net.URI

// todo maybe remove manually all prefixes '/' from all paths in code?
class AssetsURIHandler(private val assetManager: AssetManager) : URIHandler {
    override fun open(uri: URI): InputStream = assetManager.open(uri.path.removePrefix("/"))

    override fun children(uri: URI): List<URI> = assetManager.list(uri.path.removePrefix("/").removeSuffix("/"))!!.map {
        uri.resolve(it)
    }
}