package com.dmi.util.android.io

import android.net.Uri
import java.net.URI

fun Uri.toJavaURI() = URI(scheme, userInfo, host, port, path, query, fragment)