package com.dmi.perfectreader.book

import android.net.Uri
import com.dmi.perfectreader.dataAccessAsync
import com.dmi.perfectreader.data.UserData
import com.dmi.perfectreader.book.content.Content
import com.dmi.perfectreader.book.location.Location

class BookData(
        private val userData: UserData,
        private val uri: Uri,
        val content: Content
) {
    var location = userData.loadBookLocation(uri) ?: Location(0.0)
        set(value) {
            dataAccessAsync {
                userData.saveBookLocation(uri, value)
            }
            field = value
        }
}