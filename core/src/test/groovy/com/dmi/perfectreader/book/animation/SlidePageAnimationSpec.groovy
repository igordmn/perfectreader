package com.dmi.perfectreader.book.animation

import com.google.common.primitives.Floats
import com.google.common.primitives.Ints
import spock.lang.Specification

import static com.dmi.util.hamcrest.HamcrestMatchers.arrayCloseTo
import static spock.util.matcher.HamcrestMatchers.closeTo
import static spock.util.matcher.HamcrestSupport.that

class SlidePageAnimationSpec extends Specification {
    SlidePageAnimation animation = new SlidePageAnimation()
    TestPageDrawer pageDrawer = new TestPageDrawer()

    def "should move to next page"(float pageWidth, float timeForOnePage) {
        given:
        animation.setPageWidth(pageWidth)
        animation.setTimeForOnePage(timeForOnePage)
        animation.reset()

        expect:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0, 0.001f)

        when:
        animation.moveNext()

        then:
        animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(pageWidth, 0.001f)

        when:
        animation.update(timeForOnePage / 2 as float)

        then:
        float timeForStop = SlidePageAnimation.computeTimeByDistance(1, timeForOnePage)
        float distanceInPages = SlidePageAnimation.computeDistanceByTime(timeForStop - timeForOnePage / 2 as float, timeForOnePage)
        animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(distanceInPages * pageWidth, 0.001f)

        when:
        animation.update(timeForOnePage / 2 + 0.1f as float)

        then:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        when:
        animation.update(0.5f)

        then:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        where:
        [pageWidth, timeForOnePage] << getPageTimeConfigurations()
    }

    def "should move to preview page"(float pageWidth, float timeForOnePage) {
        given:
        animation.setPageWidth(pageWidth)
        animation.setTimeForOnePage(timeForOnePage)
        animation.reset()

        expect:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        when:
        animation.movePreview()

        then:
        animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(-pageWidth, 0.001f)

        when:
        animation.update(timeForOnePage / 2 as float)

        float timeForStop = SlidePageAnimation.computeTimeByDistance(1, timeForOnePage)
        float distanceInPages = -SlidePageAnimation.computeDistanceByTime(timeForStop - timeForOnePage / 2 as float, timeForOnePage)
        then:
        animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(distanceInPages * pageWidth, 0.001f)

        when:
        animation.update(timeForOnePage / 2 + 0.1f as float)

        then:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        when:
        animation.update(0.5f)

        then:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        where:
        [pageWidth, timeForOnePage] << getPageTimeConfigurations()
    }

    def "should move to second next page"(float pageWidth, float timeForOnePage) {
        given:
        animation.setPageWidth(pageWidth)
        animation.setTimeForOnePage(timeForOnePage)
        animation.reset()

        expect:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        when:
        animation.moveNext()
        animation.moveNext()

        then:
        animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(2 * pageWidth, 0.001f)

        when:
        float timeForStop = SlidePageAnimation.computeTimeByDistance(2, timeForOnePage)
        float timeForFirstNextPage = timeForStop - SlidePageAnimation.computeTimeByDistance(1, timeForOnePage)
        animation.update(timeForFirstNextPage)

        then:
        animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(pageWidth, 0.001f)

        when:
        animation.update(timeForOnePage)

        then:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        where:
        [pageWidth, timeForOnePage] << getPageTimeConfigurations()
    }

    def "should move to next page, then move to preview page"(float pageWidth, float timeForOnePage) {
        given:
        animation.setPageWidth(pageWidth)
        animation.setTimeForOnePage(timeForOnePage)
        animation.reset()

        expect:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        when:
        animation.moveNext()
        animation.movePreview()

        then:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        where:
        [pageWidth, timeForOnePage] << getPageTimeConfigurations()
    }

    def "should move to next page, then after short wait move to preview page"(float pageWidth, float timeForOnePage) {
        given:
        animation.setPageWidth(pageWidth)
        animation.setTimeForOnePage(timeForOnePage)
        animation.reset()

        expect:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        when:
        animation.moveNext()
        animation.update(timeForOnePage / 2 as float)
        animation.movePreview()

        then:
        animation.isPagesMoving()
        float timeForStop = SlidePageAnimation.computeTimeByDistance(1, timeForOnePage)
        float distanceInPages = SlidePageAnimation.computeDistanceByTime(timeForStop - timeForOnePage / 2 as float, timeForOnePage)
        that animation.destinationPageOffset(), closeTo(distanceInPages * pageWidth - pageWidth, 0.001f)

        when:
        timeForStop = SlidePageAnimation.computeTimeByDistance(-animation.destinationPageOffset() / pageWidth as float, timeForOnePage)
        animation.update(timeForStop)

        then:
        !animation.isPagesMoving()
        that animation.destinationPageOffset(), closeTo(0f, 0.001f)

        where:
        [pageWidth, timeForOnePage] << getPageTimeConfigurations()
    }

    def "moving function is correct"(float timeForOnePage) {
        when:
        float t0 = SlidePageAnimation.computeTimeByDistance(0, timeForOnePage)
        float t1 = SlidePageAnimation.computeTimeByDistance(1, timeForOnePage)
        float t2 = SlidePageAnimation.computeTimeByDistance(5, timeForOnePage)
        float t3 = SlidePageAnimation.computeTimeByDistance(70, timeForOnePage)

        float d0 = SlidePageAnimation.computeDistanceByTime(t0, timeForOnePage)
        float d1 = SlidePageAnimation.computeDistanceByTime(t1, timeForOnePage)
        float d2 = SlidePageAnimation.computeDistanceByTime(t2, timeForOnePage)
        float d3 = SlidePageAnimation.computeDistanceByTime(t3, timeForOnePage)

        then:
        // Монотонность t(d)
        that t0, closeTo(0f, 0.001f)
        t1 > t0
        t2 > t1
        t3 > t2

        // Монотонность d(t)
        that d0, closeTo(0f, 0.001f)
        d1 > d0
        d2 > d1
        d3 > d2

        // Обратимость
        that d0, closeTo(0f, 0.001f)
        that d1, closeTo(1f, 0.001f)
        that d2, closeTo(5f, 0.001f)
        that d3, closeTo(70f, 0.001f)

        expect:
        // Отрицательные значения
        that SlidePageAnimation.computeDistanceByTime(-1, timeForOnePage), closeTo(0f, 0.001f)
        that SlidePageAnimation.computeTimeByDistance(-1, timeForOnePage), closeTo(0f, 0.001f)

        // Time for one page
        that SlidePageAnimation.computeTimeByDistance(1, timeForOnePage), closeTo(timeForOnePage, 0.001f)

        where:
        timeForOnePage << [1f, 0.2f, 2.5f, 100f]
    }

    def "should draw not moving pages"(float pageWidth, float timeForOnePage) {
        when:
        animation.setPageWidth(pageWidth)
        animation.setTimeForOnePage(timeForOnePage)
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(0)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [0]
        that pageDrawer.getDrawingPagesPositions() as Number[], arrayCloseTo([0f] as Number[], 0.001f)

        where:
        [pageWidth, timeForOnePage] << getPageTimeConfigurations()
    }

    def "should draw moving to next page"(float pageWidth, float timeForOnePage) {
        when:
        animation.setPageWidth(pageWidth)
        animation.setTimeForOnePage(timeForOnePage)
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(pageWidth * 1 / 4 as float)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [-1, 0]
        that pageDrawer.getDrawingPagesPositions() as Number[],
                arrayCloseTo([-pageWidth * 3 / 4, pageWidth * 1 / 4] as Number[], 0.001f)


        when:
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(pageWidth * 3 / 4 as float)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [-1, 0]
        that pageDrawer.getDrawingPagesPositions() as Number[],
                arrayCloseTo([-pageWidth * 1 / 4, pageWidth * 3 / 4] as Number[], 0.001f)


        when:
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(pageWidth)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [-1]
        that pageDrawer.getDrawingPagesPositions() as Number[], arrayCloseTo([0f] as Number[], 0.001f)


        when:
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(pageWidth * 5 / 4 as float)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [-2, -1]
        that pageDrawer.getDrawingPagesPositions() as Number[],
                arrayCloseTo([-pageWidth * 3 / 4, pageWidth * 1 / 4] as Number[], 0.001f)


        when:
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(pageWidth * 2 as float)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [-2]
        that pageDrawer.getDrawingPagesPositions() as Number[], arrayCloseTo([0f] as Number[], 0.001f)

        where:
        [pageWidth, timeForOnePage] << getPageTimeConfigurations()
    }

    def "should draw moving to preview page"(float pageWidth, float timeForOnePage) {
        when:
        animation.setPageWidth(pageWidth)
        animation.setTimeForOnePage(timeForOnePage)
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(-pageWidth * 1 / 4 as float)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [0, 1]
        that pageDrawer.getDrawingPagesPositions() as Number[],
                arrayCloseTo([-pageWidth * 1 / 4, pageWidth * 3 / 4] as Number[], 0.001f)


        when:
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(-pageWidth * 3 / 4 as float)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [0, 1]
        that pageDrawer.getDrawingPagesPositions() as Number[],
                arrayCloseTo([-pageWidth * 3 / 4, pageWidth * 1 / 4] as Number[], 0.001f)


        when:
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(-pageWidth)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [1]
        that pageDrawer.getDrawingPagesPositions() as Number[], arrayCloseTo([0f] as Number[], 0.001f)


        when:
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(-pageWidth * 5 / 4 as float)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [1, 2]
        that pageDrawer.getDrawingPagesPositions() as Number[],
                arrayCloseTo([-pageWidth * 1 / 4, pageWidth * 3 / 4] as Number[], 0.001f)


        when:
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(-pageWidth * 2 as float)
        animation.drawPages(pageDrawer, pageWidth)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [2]
        that pageDrawer.getDrawingPagesPositions() as Number[], arrayCloseTo([0f] as Number[], 0.001f)

        where:
        [pageWidth, timeForOnePage] << getPageTimeConfigurations()
    }

    def "should draw pages on big screen"(float pageWidth, float timeForOnePage) {
        when:
        animation.setPageWidth(pageWidth)
        animation.setTimeForOnePage(timeForOnePage)
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(0)
        animation.drawPages(pageDrawer, pageWidth * 17 / 16 as float)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [0, 1]
        that pageDrawer.getDrawingPagesPositions() as Number[], arrayCloseTo([0f, pageWidth] as Number[], 0.001f)


        when:
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(pageWidth * 1 / 4 as float)
        animation.drawPages(pageDrawer, pageWidth * 17 / 16 as float)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [-1, 0]
        that pageDrawer.getDrawingPagesPositions() as Number[],
                arrayCloseTo([-pageWidth * 3 / 4, pageWidth * 1 / 4] as Number[], 0.001f)

        where:
        [pageWidth, timeForOnePage] << getPageTimeConfigurations()
    }

    def "should draw pages on small screen"(float pageWidth, float timeForOnePage) {
        when:
        animation.setPageWidth(pageWidth)
        animation.setTimeForOnePage(timeForOnePage)
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(0)
        animation.drawPages(pageDrawer, pageWidth * 3 / 16 as float)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [0]
        that pageDrawer.getDrawingPagesPositions() as Number[], arrayCloseTo([0f] as Number[], 0.001f)


        when:
        pageDrawer.clearInfo()
        animation.setDestinationPageOffset(pageWidth * 1 / 4 as float)
        animation.drawPages(pageDrawer, pageWidth * 3 / 16 as float)

        then:
        pageDrawer.getDrawingPagesRelativeNumbers() == [-1]
        that pageDrawer.getDrawingPagesPositions() as Number[], arrayCloseTo([-pageWidth * 3 / 4] as Number[], 0.001f)

        where:
        [pageWidth, timeForOnePage] << getPageTimeConfigurations()
    }

    static List getPageTimeConfigurations() {
        [[200f, 1f, 0.1f], [1f, 0.2f, 2.5f, 100f]].combinations()
    }

    static class TestPageDrawer implements PageAnimation.PageDrawer {
        private final List<Integer> relativeIndices = new ArrayList<Integer>()
        private final List<Float> positions = new ArrayList<Float>()

        public int[] getDrawingPagesRelativeNumbers() {
            return Ints.toArray(relativeIndices)
        }

        public float[] getDrawingPagesPositions() {
            return Floats.toArray(positions)
        }

        public void clearInfo() {
            relativeIndices.clear()
            positions.clear()
        }

        @Override
        public void drawPage(int relativeIndex, float posX) {
            relativeIndices.add(relativeIndex)
            positions.add(posX)
        }
    }
}
