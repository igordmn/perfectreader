package com.dmi.util.io

import java.io.InputStream
import java.net.URI

class ProtocolURIHandler(private val protocolToHandler: Map<String, URIHandler>): URIHandler {
    override fun open(uri: URI): InputStream = protocolToHandler[uri.scheme]!!.open(uri)
    override fun children(uri: URI): List<URI> = protocolToHandler[uri.scheme]!!.children(uri)
}