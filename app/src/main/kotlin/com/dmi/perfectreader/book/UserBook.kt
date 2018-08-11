package com.dmi.perfectreader.book

import android.net.Uri
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.perfectreader.common.UserData
import com.dmi.util.lang.afterSet
import com.dmi.util.scope.Scoped

suspend fun userBook(
        userData: UserData,
        uri: Uri
) = UserBook(
        userData,
        uri,
        userData.loadBookLocation(uri) ?: Location(0.0)
)

class UserBook(
        private val userData: UserData,
        private val uri: Uri,
        initialLocation: Location
) : Scoped by Scoped.Impl() {
    var location by scope.value(initialLocation).afterSet(::saveLocation)

    private fun saveLocation(location: Location) = userData.saveBookLocation(uri, location)
}