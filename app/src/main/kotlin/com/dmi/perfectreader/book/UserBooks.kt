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
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.parseOpt
import org.jetbrains.anko.db.replace
import java.util.*

class UserBooks(private val db: SQLiteDatabase) {
    suspend fun load(uri: Uri): Book? = withContext(Dispatchers.IO) {
        db.execQuery("SELECT uri, offset, percent, lastReadTime FROM UserBook WHERE uri = {uri}", "uri" to uri.toString()) {
            parseOpt(parser())
        }
    }

    suspend fun lastBooks(count: Int): List<Book> = withContext(Dispatchers.IO) {
        db.execQuery("SELECT uri, offset, percent, lastReadTime FROM UserBook ORDER BY lastReadTime DESC LIMIT $count") {
            parseList(parser())
        }
    }

    private fun parser() = object : MapRowParser<Book> {
        override fun parseRow(columns: Map<String, Any?>) = Book(
                uri = Uri.parse(columns["uri"] as String),
                location = Location(columns["offset"] as Double),
                percent = columns["percent"] as Double,
                lastReadTime = Date(columns["lastReadTime"] as Long)
        )
    }

    fun save(book: Book) {
        GlobalScope.launch(Dispatchers.IO) {
            db.replace("UserBook",
                    "uri" to book.uri.toString(),
                    "offset" to book.location.offset,
                    "percent" to book.percent,
                    "lastReadTime" to book.lastReadTime.time
            )
        }
    }

    // todo remove percent, add endPagePercent. or use location everywhere as end page location
    class Book(val uri: Uri, val location: Location, val percent: Double, val lastReadTime: Date)
}