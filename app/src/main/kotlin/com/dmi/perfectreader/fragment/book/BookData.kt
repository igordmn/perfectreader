package com.dmi.perfectreader.fragment.book

import android.net.Uri
import com.dmi.perfectreader.app.dataAccessAsync
import com.dmi.perfectreader.data.UserData
import com.dmi.perfectreader.fragment.book.content.Content
import com.dmi.perfectreader.fragment.book.location.Location

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