package com.dmi.util.io

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URI

class FileURIHandler: URIHandler {
    override fun open(uri: URI): InputStream = FileInputStream(uri.path)
    override fun children(uri: URI): List<URI> = File(uri.path).list().map { uri.resolve(it) }
}