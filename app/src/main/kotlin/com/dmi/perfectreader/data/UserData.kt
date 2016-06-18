package com.dmi.perfectreader.data

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.dmi.perfectreader.fragment.book.location.Location
import com.dmi.util.ext.execQuery
import com.dmi.util.lang.returnUnit
import org.jetbrains.anko.db.DoubleParser
import org.jetbrains.anko.db.StringParser
import org.jetbrains.anko.db.parseOpt
import org.jetbrains.anko.db.replace

class UserData(private val userDatabase: SQLiteDatabase) {
    fun loadLastBookURI() =
            userDatabase.execQuery("SELECT uri FROM LastBook where id = 1") {
                parseOpt(StringParser)?.let { Uri.parse(it) }
            }

    fun saveLastBookFile(uri: Uri) = userDatabase.replace("LastBook",
            "id" to 1,
            "uri" to uri.toString()
    )

    fun loadBookLocation(uri: Uri) =
            userDatabase.execQuery("SELECT offset FROM BookLocation WHERE uri = {uri}",
                    "uri" to uri.toString()
            ) {
                parseOpt(DoubleParser)?.let { Location(it) }
            }

    fun saveBookLocation(uri: Uri, location: Location) = userDatabase.replace("BookLocation",
            "uri" to uri.toString(),
            "offset" to location.offset
    ).returnUnit()
}