package com.dmi.util.io

import java.io.FileInputStream
import java.io.InputStream
import java.net.URI

class FileURIHandler(): URIHandler {
    override fun open(uri: URI): InputStream = FileInputStream(uri.path)
}