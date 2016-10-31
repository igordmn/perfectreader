package com.dmi.util.android.io

import android.content.res.AssetManager
import com.dmi.util.io.URIHandler
import java.io.InputStream
import java.net.URI

class AssetsURIHandler(private val assetManager: AssetManager) : URIHandler {
    override fun open(uri: URI): InputStream = assetManager.open(uri.path.removePrefix("/"))
}