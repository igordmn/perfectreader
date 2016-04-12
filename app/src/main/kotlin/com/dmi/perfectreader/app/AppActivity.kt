package com.dmi.perfectreader.app

import android.content.Intent
import android.os.Bundle
import com.dmi.perfectreader.bookreader.BookReaderController
import com.dmi.util.base.BaseActivity
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class AppActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initRootController { createRootController() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val requestedBookFile = requestedBookFile(intent)
        if (requestedBookFile != null)
            setRootController(createRootController())
    }

    fun createRootController() = BookReaderController(requestedBookFile(intent))

    private fun requestedBookFile(intent: Intent): File? {
        val data = intent.data
        try {
            if (data != null) {
                val path = URLDecoder.decode(data.encodedPath, "UTF-8")
                return File(path)
            } else {
                return null
            }
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException()
        }
    }
}
