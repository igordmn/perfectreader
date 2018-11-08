package com.dmi.perfectreader

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.dmi.perfectreader.library.LibraryActivity
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
        finish()
        val context = this
        launch {
            if (main.permissions.askReadStorage()) {
                startActivity(Intent(context, LibraryActivity::class.java))
            } else {
                toast(R.string.needStoragePermissions)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}