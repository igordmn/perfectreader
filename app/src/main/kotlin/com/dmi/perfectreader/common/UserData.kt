package com.dmi.perfectreader.common

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.util.android.ext.execQuery
import com.dmi.util.coroutine.IOPool
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import org.jetbrains.anko.db.DoubleParser
import org.jetbrains.anko.db.StringParser
import org.jetbrains.anko.db.parseOpt
import org.jetbrains.anko.db.replace

class UserData(private val userDatabase: SQLiteDatabase) {
    suspend fun loadLastBookURI(): Uri? = withContext(IOPool) {
        userDatabase.execQuery("SELECT uri FROM LastBook where id = 1") {
            parseOpt(StringParser)?.let { Uri.parse(it) }
        }
    }

    fun saveLastBookFile(uri: Uri) {
        launch(IOPool) {
            userDatabase.replace("LastBook",
                    "id" to 1,
                    "uri" to uri.toString()
            )
        }
    }

    suspend fun loadBookLocation(uri: Uri): Location? = withContext(IOPool) {
        userDatabase.execQuery("SELECT offset FROM BookLocation WHERE uri = {uri}",
                "uri" to uri.toString()
        ) {
            parseOpt(DoubleParser)?.let { Location(it) }
        }
    }

    fun saveBookLocation(uri: Uri, location: Location) {
        launch(IOPool) {
            userDatabase.replace("BookLocation",
                    "uri" to uri.toString(),
                    "offset" to location.offset
            )
        }
    }
}