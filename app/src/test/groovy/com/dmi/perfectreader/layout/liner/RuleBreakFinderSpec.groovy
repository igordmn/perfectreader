package com.dmi.perfectreader.layout.liner

import com.dmi.perfectreader.layout.liner.BreakFinder.Break
import com.dmi.perfectreader.layout.wordbreak.WordBreaker
import spock.lang.Specification

class RuleBreakFinderSpec extends Specification {
    def "break simple text"() {
        given:
        def breakFinder = new RuleBreakFinder(nonWordBreaker())

        when:
        def breaks = findBreaks(breakFinder, "simple text simple text, text")

        then:
        indicesOf(breaks) == [7, 12, 19, 25, 29]
    }

    def "break spaces"() {
        given:
        def breakFinder = new RuleBreakFinder(nonWordBreaker())

        when:
        def breaks = findBreaks(breakFinder, "simple    text  sim\u00A0ple tex\nt    \n    text text  \u00A0\u00A0\n\n  ")

        then:
        indicesOf(breaks) == [10, 16, 24, 28, 34, 38, 43, 52, 53, 55]
        forcesIn(breaks) == [28, 34, 52, 53]
    }

    def "break objects"() {
        given:
        def breakFinder = new RuleBreakFinder(nonWordBreaker())

        when:
        def breaks = findBreaks(breakFinder, "simple  \n\uFFFC\uFFFC  te\uFFFCxt\uFFFC")

        then:
        indicesOf(breaks) == [9, 10, 11, 13, 15, 16, 18, 19]
        forcesIn(breaks) == [9]
    }

    def "break words"() {
        given:
        def breakFinder = new RuleBreakFinder(
                definedWordBreaker([
                        "simple": [3],
                        "text": [1],           // should not break at 1 because of \n char in test text
                        ("te\nxt"): [1],       // should not break at 1 because of \n char in test text
                        "word": [2],
                        "bigword": [3],
                        "bigbigword": [3, 6],
                ])
        )

        when:
        def breaks = findBreaks(breakFinder, "simple   \n  te\nxt word bigbigword, big-bigword")

        then:
        indicesOf(breaks) == [3, 10, 12, 15, 18, 20, 23, 26, 29, 35, 39, 42, 46]
        hasHyphensIn(breaks) == [3, 20, 26, 29, 42]
        forcesIn(breaks) == [10, 15]
    }

    def findBreaks(BreakFinder breakFinder, CharSequence text) {
        def breaks = []
        breakFinder.findBreaks(text, Locale.US, {
            breaks.add(wrapBreak(it))
        })
        return breaks
    }

    def wrapBreak(Break br) {
        return [
                index: br.index(),
                hasHyphen: br.hasHyphen(),
                isForce: br.isForce(),
        ]
    }

    def indicesOf(breaks) {
        return breaks.collect({ it.index })
    }

    def hasHyphensIn(breaks) {
        return breaks.
                findAll({ it.hasHyphen }).
                collect({ it.index })
    }

    def forcesIn(breaks) {
        return breaks.
                findAll({ it.isForce }).
                collect({ it.index })
    }

    def nonWordBreaker() {
        return new WordBreaker() {
            @Override
            WordBreaker.WordBreaks breakWord(CharSequence text, Locale locale, int beginIndex, int endIndex) {
                return wordBreaks(beginIndex, [])
            }
        }
    }
    
    def definedWordBreaker(Map<String, List<Integer>> wordToBreakIndices) {
        return new WordBreaker() {
            @Override
            WordBreaker.WordBreaks breakWord(CharSequence text, Locale locale, int beginIndex, int endIndex) {
                def word = text.subSequence(beginIndex, endIndex)
                for (entry in wordToBreakIndices) {
                    if (word == entry.key) {
                        return wordBreaks(beginIndex, entry.value)
                    }
                }
                return wordBreaks(beginIndex, [])
            }
        }
    }

    def wordBreaks(int beginIndex, List<Integer> indices) {
        return new WordBreaker.WordBreaks() {
            @Override
            boolean canBreakBefore(int index) {
                return indices.contains(index - beginIndex)
            }
        }
    }
}
