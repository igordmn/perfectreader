package com.dmi.perfectreader.reader

import android.net.Uri
import com.dmi.perfectreader.Main
import com.dmi.util.log.Log
import com.dmi.util.scope.Scope
import com.dmi.util.scope.observable
import com.dmi.util.screen.Screen
import kotlinx.serialization.Serializable
import java.io.IOException

class ReaderLoad(
        private val main: Main,
        private val uri: Uri,
        val back: () -> Unit,
        val state: ReaderLoadState,
        private val log: Log = main.log,
        scope: Scope = Scope()
) : Screen by Screen(scope) {
    var isLoading: Boolean by observable(true)
    var error: LoadError? by observable(null)
    var reader: Reader? by observable(null)

    init {
        scope.launch {
            try {
                reader = reader(main, uri, state.reader)
            } catch (e: IOException) {
                log.e(e, "Book load error")
                error = LoadError.IO()
            }
            isLoading = false
        }
    }

    sealed class LoadError : Exception() {
        class IO : LoadError()
    }
}

@Serializable
class ReaderLoadState(val reader: ReaderState = ReaderState())