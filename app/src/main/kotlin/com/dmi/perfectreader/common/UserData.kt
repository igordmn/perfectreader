package com.dmi.perfectreader.common

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.util.android.db.execQuery
import kotlinx.coroutines.*
import org.jetbrains.anko.db.DoubleParser
import org.jetbrains.anko.db.StringParser
import org.jetbrains.anko.db.parseOpt
import org.jetbrains.anko.db.replace

class UserData(private val userDatabase: SQLiteDatabase) {
    suspend fun loadLastBookURI(): Uri? = withContext(Dispatchers.IO) {
        userDatabase.execQuery("SELECT uri FROM LastBook where id = 1") {
            parseOpt(StringParser)?.let { Uri.parse(it) }
        }
    }

    fun saveLastBookFile(uri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            userDatabase.replace("LastBook",
                    "id" to 1,
                    "uri" to uri.toString()
            )
        }
    }

    suspend fun loadBookLocation(uri: Uri): Location? = withContext(Dispatchers.IO) {
        userDatabase.execQuery("SELECT offset FROM BookLocation WHERE uri = {uri}",
                "uri" to uri.toString()
        ) {
            parseOpt(DoubleParser)?.let { Location(it) }
        }
    }

    fun saveBookLocation(uri: Uri, location: Location) {
        GlobalScope.launch(Dispatchers.IO) {
            userDatabase.replace("BookLocation",
                    "uri" to uri.toString(),
                    "offset" to location.offset
            )
        }
    }
}