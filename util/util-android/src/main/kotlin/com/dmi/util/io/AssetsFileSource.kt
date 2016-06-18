package com.dmi.util.io

import android.content.res.AssetManager
import com.google.common.io.ByteSource

class AssetsFileSource(
        private val assets: AssetManager,
        private val path: String
) : ByteSource() {
    override fun openStream() = assets.open(path)
}