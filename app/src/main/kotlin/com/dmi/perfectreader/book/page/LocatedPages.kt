package com.dmi.perfectreader.book.page

import android.net.Uri
import com.dmi.perfectreader.book.Locations
import com.dmi.perfectreader.book.UserBooks
import com.dmi.perfectreader.book.content.location.Location
import com.dmi.util.lang.set
import com.dmi.util.lang.value
import com.dmi.util.scope.observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

suspend fun locatedPages(uri: Uri, userBooks: UserBooks): LocatedPagesReadOnly {
    suspend fun load() = userBooks.load(uri)

    fun save(locations: Locations, location: Location) {
        GlobalScope.launch(Dispatchers.IO) {
            userBooks.save(uri, UserBooks.Book(
                    location = location,
                    percent = locations.locationToPercent(location)
            ))
        }
    }

    val initialLocation = load()?.location ?: Location(0.0)
    return object : LocatedPagesReadOnly {
        override var location: Location by observable(initialLocation)

        override fun saveable(locations: Locations): LocatedPages {
            fun save(location: Location) = save(locations, location)
            val self = this
            return object : LocatedPages {
                override var location: Location by value(self::location).set(::save)
            }
        }
    }
}

interface LocatedPagesReadOnly {
    val location: Location
    fun saveable(locations: Locations): LocatedPages
}

interface LocatedPages {
    var location: Location
}