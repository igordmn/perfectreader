package com.dmi.util.io

import java.io.InputStream
import java.net.URI

interface URIHandler {
    fun open(uri: URI): InputStream
    fun children(uri: URI): List<URI>
}