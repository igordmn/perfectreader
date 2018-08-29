package com.dmi.perfectreader.reader

import android.content.Intent
import android.net.Uri
import com.dmi.perfectreader.Main
import com.dmi.util.android.system.Permissions
import com.dmi.perfectreader.common.UserData
import com.dmi.util.log.Log
import com.dmi.util.scope.Scoped
import com.dmi.util.system.ApplicationWindow
import java.io.IOException

class ReaderLoad(
        private val main: Main,
        private val window: ApplicationWindow,
        private val intent: Intent,
        private val log: Log = main.log,
        private val userData: UserData = main.userData,
        private val permissions: Permissions = main.permissions
) : Scoped by Scoped.Impl() {
    var isLoading: Boolean by scope.value(true)
    var error: LoadError? by scope.value(null)
    var reader: Reader? by scope.value(null)

    init {
        scope.launch {
            try {
                val uri = bookURI(intent)
                if (uri != null) {
                    if (permissions.askStorage()) {
                        reader = reader(main, window, uri)
                    } else {
                        error = LoadError.NeedStoragePermissions()
                    }
                } else {
                    error = LoadError.NeedOpenThroughFileManager()
                }
            } catch (e: IOException) {
                log.e(e, "Book load error")
                error = LoadError.IO()
            }
            isLoading = false
        }
    }

    private suspend fun bookURI(intent: Intent): Uri? {
        val requestedURI = intent.data
        return if (requestedURI != null) {
            userData.saveLastBookFile(requestedURI)
            requestedURI
        } else {
            userData.loadLastBookURI()
        }
    }

    fun close() = window.close()

    sealed class LoadError : Exception() {
        class IO : LoadError()
        class NeedOpenThroughFileManager : LoadError()
        class NeedStoragePermissions : LoadError()
    }
}