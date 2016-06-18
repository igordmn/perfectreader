package com.dmi.perfectreader.fragment.book

import android.net.Uri
import com.dmi.perfectreader.app.dataAccessAsync
import com.dmi.perfectreader.data.UserData
import com.dmi.perfectreader.fragment.book.content.BookContent
import com.dmi.perfectreader.fragment.book.location.Location
import rx.lang.kotlin.BehaviorSubject

class BookData(
        private val userData: UserData,
        private val uri: Uri,
        val content: BookContent
) {
    val locationObservable = BehaviorSubject<Location>()

    var location = userData.loadBookLocation(uri) ?: Location(0.0)
        set(value) {
            dataAccessAsync {
                userData.saveBookLocation(uri, value)
            }
            locationObservable.onNext(value)
            field = value
        }

    init {
        locationObservable.onNext(location)
    }
}