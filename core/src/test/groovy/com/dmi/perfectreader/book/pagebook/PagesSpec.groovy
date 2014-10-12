package com.dmi.perfectreader.book.pagebook

import spock.lang.Specification

import static com.dmi.perfectreader.book.content.Content.emptyContent
import static com.dmi.perfectreader.book.position.Position.percentToPosition

class PagesSpec extends Specification {
    def "should put single page"() {
        given:
        def pages = new Pages(1)
        def page30_40 = emptyContent percentToPosition(0.30), percentToPosition(0.40)

        when:
        pages.put(page30_40)

        then:
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.30))
        pages.put(page30_40)

        then:
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == null

        when:
        pages.put(page30_40)

        then:
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.39))

        then:
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.40))

        then:
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null

        when:
        pages.put(page30_40)

        then:
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.39))
        pages.put(page30_40)

        then:
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.30))

        then:
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.29))

        then:
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.30))

        then:
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null
    }

    def "should put two pages"() {
        given:
        def pages = new Pages(1)
        def page30_40 = emptyContent percentToPosition(0.30), percentToPosition(0.40)
        def page40_50 = emptyContent percentToPosition(0.40), percentToPosition(0.50)

        when:
        pages.put(page30_40)
        pages.put(page40_50)

        then:
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.30))
        pages.put(page30_40)

        then:
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == null

        when:
        pages.put(page40_50)

        then:
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == page40_50

        when:
        pages.put(page30_40)
        pages.put(page30_40)
        pages.put(page30_40)
        pages.put(page40_50)
        pages.put(page40_50)
        pages.put(page40_50)

        then:
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == page40_50

        when:
        pages.setMiddle(percentToPosition(0.40))

        then:
        pages.get(-1) == page30_40
        pages.get(0) == page40_50
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.49))

        then:
        pages.get(-1) == page30_40
        pages.get(0) == page40_50
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.50))

        then:
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null

        when:
        pages.put(page30_40)
        pages.put(page40_50)

        then:
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.49))
        pages.put(page30_40)
        pages.put(page40_50)

        then:
        pages.get(-1) == null
        pages.get(0) == page40_50
        pages.get(1) == null

        when:
        pages.put(page30_40)

        then:
        pages.get(-1) == page30_40
        pages.get(0) == page40_50
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.30))

        then:
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == page40_50

        when:
        pages.setMiddle(percentToPosition(0.29))

        then:
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null

        when:
        pages.setMiddle(percentToPosition(0.30))

        then:
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null
    }

    def "should put many pages"() {
        given:
        def pages = new Pages(3)
        def page30_40 = emptyContent percentToPosition(0.30), percentToPosition(0.40)
        def page35_45 = emptyContent percentToPosition(0.35), percentToPosition(0.45)
        def page45_55 = emptyContent percentToPosition(0.45), percentToPosition(0.55)
        def page46_56 = emptyContent percentToPosition(0.46), percentToPosition(0.56)
        def page47_57 = emptyContent percentToPosition(0.47), percentToPosition(0.57)
        def page50_60 = emptyContent percentToPosition(0.50), percentToPosition(0.60)
        def page60_70 = emptyContent percentToPosition(0.60), percentToPosition(0.70)

        when:
        pages.setMiddle(percentToPosition(0.30))
        pages.put(page30_40)
        pages.put(page35_45)
        pages.put(page45_55)
        pages.put(page46_56)
        pages.put(page47_57)
        pages.put(page50_60)
        pages.put(page60_70)

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == page35_45
        pages.get(2) == page45_55
        pages.get(3) == page46_56

        when:
        pages.setMiddle(percentToPosition(0.35))

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == page30_40
        pages.get(0) == page35_45
        pages.get(1) == page45_55
        pages.get(2) == page46_56
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.55))

        then:
        pages.get(-3) == page30_40
        pages.get(-2) == page35_45
        pages.get(-1) == page45_55
        pages.get(0) == page46_56
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.56))

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.55))
        pages.put(page60_70)
        pages.put(page50_60)
        pages.put(page47_57)
        pages.put(page46_56)
        pages.put(page45_55)
        pages.put(page35_45)
        pages.put(page30_40)

        then:
        pages.get(-3) == page45_55
        pages.get(-2) == page46_56
        pages.get(-1) == page47_57
        pages.get(0) == page50_60
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.59))

        then:
        pages.get(-3) == page45_55
        pages.get(-2) == page46_56
        pages.get(-1) == page47_57
        pages.get(0) == page50_60
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.56))

        then:
        pages.get(-3) == page45_55
        pages.get(-2) == page46_56
        pages.get(-1) == page47_57
        pages.get(0) == page50_60
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.50))

        then:
        pages.get(-3) == page45_55
        pages.get(-2) == page46_56
        pages.get(-1) == page47_57
        pages.get(0) == page50_60
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.49))

        then:
        pages.get(-3) == null
        pages.get(-2) == page45_55
        pages.get(-1) == page46_56
        pages.get(0) == page47_57
        pages.get(1) == page50_60
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.47))

        then:
        pages.get(-3) == null
        pages.get(-2) == page45_55
        pages.get(-1) == page46_56
        pages.get(0) == page47_57
        pages.get(1) == page50_60
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.46))

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == page45_55
        pages.get(0) == page46_56
        pages.get(1) == page47_57
        pages.get(2) == page50_60
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.45))

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page45_55
        pages.get(1) == page46_56
        pages.get(2) == page47_57
        pages.get(3) == page50_60

        when:
        pages.setMiddle(percentToPosition(0.50))

        then:
        pages.get(-3) == page45_55
        pages.get(-2) == page46_56
        pages.get(-1) == page47_57
        pages.get(0) == page50_60
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.put(page60_70)

        then:
        pages.get(-3) == page45_55
        pages.get(-2) == page46_56
        pages.get(-1) == page47_57
        pages.get(0) == page50_60
        pages.get(1) == page60_70
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.44))

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.setMiddle(percentToPosition(0.50))

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == null
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null
    }

    def "should overlap pages"() {
        given:
        def pages = new Pages(3)
        def page30_40 = emptyContent percentToPosition(0.30), percentToPosition(0.40)
        def page35_45 = emptyContent percentToPosition(0.35), percentToPosition(0.45)
        def page45_55 = emptyContent percentToPosition(0.45), percentToPosition(0.55)
        def page30_39 = emptyContent percentToPosition(0.30), percentToPosition(0.39)
        def page31_40 = emptyContent percentToPosition(0.31), percentToPosition(0.40)
        def page30_41 = emptyContent percentToPosition(0.30), percentToPosition(0.41)
        def page30_45 = emptyContent percentToPosition(0.30), percentToPosition(0.45)
        def page30_55 = emptyContent percentToPosition(0.30), percentToPosition(0.55)
        def page0_100 = emptyContent percentToPosition(0.00), percentToPosition(1.00)

        when:
        pages.setMiddle(percentToPosition(0.30))
        pages.put(page30_40)
        pages.put(page35_45)
        pages.put(page45_55)

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == page35_45
        pages.get(2) == page45_55
        pages.get(3) == null

        when:
        pages.put(page30_39)

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == page35_45
        pages.get(2) == page45_55
        pages.get(3) == null

        when:
        pages.put(page31_40)

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == page35_45
        pages.get(2) == page45_55
        pages.get(3) == null

        when:
        pages.put(page30_41)

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page30_41
        pages.get(1) == page35_45
        pages.get(2) == page45_55
        pages.get(3) == null

        when:
        pages.put(page30_45)

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page30_45
        pages.get(1) == page45_55
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.put(page30_40)
        pages.put(page35_45)
        pages.put(page45_55)

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page30_45
        pages.get(1) == page45_55
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.clear()
        pages.put(page30_40)
        pages.put(page35_45)
        pages.put(page45_55)

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page30_40
        pages.get(1) == page35_45
        pages.get(2) == page45_55
        pages.get(3) == null

        when:
        pages.put(page30_55)

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page30_55
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null

        when:
        pages.clear()
        pages.put(page30_40)
        pages.put(page35_45)
        pages.put(page45_55)
        pages.put(page0_100)

        then:
        pages.get(-3) == null
        pages.get(-2) == null
        pages.get(-1) == null
        pages.get(0) == page0_100
        pages.get(1) == null
        pages.get(2) == null
        pages.get(3) == null
    }

    def "cannot get page out of maxRelativeIndex"() {
        given:
        Pages pages0 = new Pages(0)
        Pages pages2 = new Pages(2)

        when:
        pages2.get(3)

        then:
        thrown(IllegalArgumentException)

        when:
        pages0.get(-1)

        then:
        thrown(IllegalArgumentException)

        when:
        pages0.get(1)

        then:
        thrown(IllegalArgumentException)

        when:
        pages2.get(-3)

        then:
        thrown(IllegalArgumentException)

        when:
        pages0.get(0)
        pages2.get(-2)
        pages2.get(-1)
        pages2.get(0)
        pages2.get(1)
        pages2.get(2)

        then:
        notThrown(IllegalArgumentException)
    }
}
