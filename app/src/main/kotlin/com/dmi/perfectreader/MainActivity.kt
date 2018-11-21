package com.dmi.perfectreader

import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.core.net.toUri
import com.dmi.perfectreader.ui.library.LibraryActivity
import com.dmi.perfectreader.ui.reader.ReaderActivity
import com.dmi.util.debug.IsDebug
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
            if (main.permissions.ask(permissions())) {
                initApplication()
                startRecentActivity()
            } else {
                toast(R.string.needStoragePermissions)
            }
            finish()
        }
    }

    private fun permissions() = defaultPermissions() + (if (IsDebug) debugPermissions() else emptyArray())

    private fun defaultPermissions() = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun debugPermissions() = arrayOf(Manifest.permission.READ_PHONE_STATE)

    private fun initApplication() = (applicationContext as MainApplication).initAfterPermissions()

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