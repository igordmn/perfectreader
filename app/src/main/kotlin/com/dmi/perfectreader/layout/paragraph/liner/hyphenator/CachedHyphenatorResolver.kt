package com.dmi.perfectreader.layout.paragraph.liner.hyphenator

import com.dmi.util.libext.cache
import java.util.*

class CachedHyphenatorResolver(private val resolver: HyphenatorResolver) : HyphenatorResolver {
    private var hyphenators = cache<Locale, Hyphenator>(maximumSize = 2) {
        resolver.hyphenatorFor(it)
    }

    override fun hyphenatorFor(locale: Locale) = hyphenators.get(locale)
}
