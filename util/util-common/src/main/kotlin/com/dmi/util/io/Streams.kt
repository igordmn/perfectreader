package com.dmi.util.io

import kotlinx.io.InputStream
import org.apache.commons.io.input.BOMInputStream

fun InputStream.withoutUtfBom() = BOMInputStream(this, false)