package com.dmi.perfectreader.app

import android.content.Intent
import android.os.Bundle
import com.dmi.perfectreader.R
import com.dmi.perfectreader.bookreader.BookReaderFragment
import com.dmi.util.base.BaseActivity
import com.dmi.util.layout.HasLayout
import me.tatarka.simplefragment.SimpleFragment
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

@HasLayout(R.layout.activity_container)
class AppActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (findChild<SimpleFragment?>(R.id.rootContainer) == null) {
            val requestedBookFile = requestedBookFile(intent)
            addChild(BookReaderFragment.intent(requestedBookFile), R.id.rootContainer)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val requestedBookFile = requestedBookFile(intent)
        if (requestedBookFile != null) {
            removeChild(R.id.rootContainer)
            addChild(BookReaderFragment.intent(requestedBookFile), R.id.rootContainer)
        }
    }

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
