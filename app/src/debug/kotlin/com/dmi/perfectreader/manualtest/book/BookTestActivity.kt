package com.dmi.perfectreader.manualtest.book

import android.os.Bundle
import android.view.KeyEvent
import com.dmi.perfectreader.book.BookController
import com.dmi.util.base.BaseActivity
import com.google.common.io.ByteStreams
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BookTestActivity : BaseActivity() {
    private lateinit var bookController: BookController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookController = initRootController { BookController(tempBook(TEST_BOOK)) }
    }

    private fun tempBook(path: String): File {
        var path = path
        path = path.substring("assets://".length)
        try {
            val fileName = File(path).name
            val tempFile = File(cacheDir, fileName)
            assets.open(path).use { stream -> FileOutputStream(tempFile).use { os -> ByteStreams.copy(stream, os) } }
            return tempFile
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            bookController.goNextPage()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            bookController.goPreviewPage()
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
                keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                super.onKeyUp(keyCode, event)
    }

    companion object {
        private val TEST_BOOK = ""
    }
}