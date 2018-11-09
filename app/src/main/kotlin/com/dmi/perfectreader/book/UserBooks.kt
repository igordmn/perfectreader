package com.dmi.perfectreader.book

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.util.android.db.execQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.parseOpt
import org.jetbrains.anko.db.replace

class UserBooks(private val db: SQLiteDatabase) {
    suspend fun load(uri: Uri): Book? = withContext(Dispatchers.IO) {
        db.execQuery("SELECT offset, percent FROM UserBook WHERE uri = {uri}", "uri" to uri.toString()) {
            parseOpt(object : MapRowParser<Book> {
                override fun parseRow(columns: Map<String, Any>) = Book(
                        location = Location(columns["offset"] as Double),
                        percent = columns["percent"] as Double
                )
            })
        }
    }

    fun save(uri: Uri, data: Book) {
        GlobalScope.launch(Dispatchers.IO) {
            db.replace("UserBook",
                    "uri" to uri.toString(),
                    "offset" to data.location.offset,
                    "percent" to data.percent
            )
        }
    }

    // todo remove percent, add endPagePercent. or use location everywhere as end page location
    class Book(val location: Location, val percent: Double)
}