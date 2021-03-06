package com.dmi.perfectreader.book.layout.paragraph.hyphenator

import com.dmi.util.cache.cache
import java.util.*

class CachedHyphenatorResolver(private val resolver: HyphenatorResolver) : HyphenatorResolver {
    private var hyphenators = cache(maximumSize = 2) { locale: Locale ->
        resolver.hyphenatorFor(locale)
    }

    override fun hyphenatorFor(locale: Locale) = hyphenators[locale]
}