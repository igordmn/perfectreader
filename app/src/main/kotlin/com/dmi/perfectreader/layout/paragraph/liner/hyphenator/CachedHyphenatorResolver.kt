package com.dmi.perfectreader.layout.paragraph.liner.hyphenator

import com.dmi.util.libext.weakValuesCache
import java.util.*

class CachedHyphenatorResolver(private val resolver: HyphenatorResolver) : HyphenatorResolver {
    private var hyphenators = weakValuesCache<Locale, Hyphenator> {
        resolver.hyphenatorFor(it)
    }

    override fun hyphenatorFor(locale: Locale) = hyphenators.get(locale)
}
