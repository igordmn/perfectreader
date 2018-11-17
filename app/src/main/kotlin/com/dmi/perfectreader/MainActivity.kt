package com.dmi.perfectreader

import android.app.Activity
import android.os.Bundle
import androidx.core.net.toUri
import com.dmi.perfectreader.ui.library.LibraryActivity
import com.dmi.perfectreader.ui.reader.ReaderActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import kotlin.coroutines.CoroutineContext

class MainActivity : Activity(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch {
            if (main.permissions.askReadStorage()) {
                startRecentActivity()
            } else {
                toast(R.string.needStoragePermissions)
            }
            finish()
        }
    }

    private fun startRecentActivity() {
        val settings = main.settings.state
        if (settings.isLibrary) {
            LibraryActivity.start(this)
        } else {
            ReaderActivity.start(this, settings.bookUri.toUri())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}