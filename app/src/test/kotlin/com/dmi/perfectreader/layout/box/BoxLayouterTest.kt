package com.dmi.perfectreader.layout.box

import com.dmi.perfectreader.layout.LayoutBox
import com.dmi.perfectreader.layout.LayoutBox.Child
import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.layout.common.LayoutLength.Absolute
import com.dmi.perfectreader.layout.common.LayoutLength.Percent
import com.dmi.perfectreader.layout.common.LayoutSize
import com.dmi.perfectreader.layout.common.LayoutSpace
import com.dmi.perfectreader.layout.common.Layouter
import com.dmi.perfectreader.render.RenderObject
import com.dmi.perfectreader.style.Align
import org.amshove.kluent.shouldEqual
import org.junit.Test
import java.util.*

@Suppress("IllegalIdentifier")
class BoxLayouterTest {
    @Test
    fun `render object with fixed size in fixed space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = LayoutBox(
                fixedSize(200F, 100F),
                Align.LEFT,
                listOf(
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(200F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(100F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(300F, 200F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FillObject()),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(2F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(0F)),
                        Child(percentMargins(1F, 2F, 0.5F, 3F), FillObject())
                )
        )

        // when
        val renderBox = layouter.layout(box, fixedSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 200F
            height shouldEqual 100F

            childWidths shouldEqual listOf(
                    200F,
                    100F,
                    300F,
                    200F - 50F - 40F,
                    2F * 200F,
                    0F * 200F,
                    0F
            )

            childHeights shouldEqual listOf(
                    20F,
                    20F,
                    200F,
                    100F - 30F - 20F,
                    2F * 100F,
                    0F * 100F,
                    0F
            )

            childXs shouldEqual listOf(
                    50F,
                    50F,
                    50F,
                    50F,
                    50F,
                    50F,
                    1F * 200
            )

            childYOffsets shouldEqual listOf(
                    30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 0.5F * 100
            )
        }
    }

    @Test
    fun `render object with fixed size in wrap space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = LayoutBox(
                fixedSize(200F, 100F),
                Align.LEFT,
                listOf(
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(200F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(100F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(300F, 200F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FillObject()),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(2F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(0F)),
                        Child(percentMargins(1F, 2F, 0.5F, 3F), FillObject())
                )
        )

        // when
        val renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 200F
            height shouldEqual 100F

            childWidths shouldEqual listOf(
                    200F,
                    100F,
                    300F,
                    200F - 50F - 40F,
                    2F * 200F,
                    0F * 200F,
                    0F
            )

            childHeights shouldEqual listOf(
                    20F,
                    20F,
                    200F,
                    100F - 30F - 20F,
                    2F * 100F,
                    0F * 100F,
                    0F
            )

            childXs shouldEqual listOf(
                    50F,
                    50F,
                    50F,
                    50F,
                    50F,
                    50F,
                    1F * 200
            )

            childYOffsets shouldEqual listOf(
                    30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 0.5F * 100
            )
        }
    }

    @Test
    fun `render object with auto size in fixed space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = LayoutBox(
                autoSize(),
                Align.LEFT,
                listOf(
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(200F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(100F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(300F, 200F)),
                        Child(fixedMargins(10F, 20F, 30F, 20F), FillObject()),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(2F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(0F)),
                        Child(percentMargins(1F, 2F, 0.5F, 3F), FillObject())
                )
        )

        // when
        var renderBox = layouter.layout(box, fixedSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 100F
            height shouldEqual 6 * (20F + 30F) +
                    20F +
                    20F +
                    200F +
                    100F - 30F - 20F +
                    0F +
                    0F +
                    100F

            childWidths shouldEqual listOf(
                    200F,
                    100F,
                    300F,
                    100F - 10F - 20F,
                    2F * 100F,
                    0F * 100F,
                    0F
            )

            childHeights shouldEqual listOf(
                    20F,
                    20F,
                    200F,
                    100F - 30F - 20F,
                    0F,
                    0F,
                    100F - 0F - 0F
            )

            childXs shouldEqual listOf(
                    50F,
                    50F,
                    50F,
                    10F,
                    50F,
                    50F,
                    1F * 100
            )

            childYOffsets shouldEqual listOf(
                    30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 0F
            )
        }
    }

    @Test
    fun `render object with auto size in wrap space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = LayoutBox(
                autoSize(),
                Align.LEFT,
                listOf(
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(200F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(100F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(300F, 200F)),
                        Child(fixedMargins(10F, 20F, 30F, 20F), FillObject()),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(2F)),
                        Child(percentMargins(0.1F, 0.2F, 0F, 0F), PercentObject(0F)),
                        Child(percentMargins(1F, 2F, 0.5F, 3F), FillObject())
                )
        )

        // when
        var renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 300F + 50F + 40F
            height shouldEqual 5 * (20F + 30F) +
                    20F +
                    20F +
                    200F +
                    100F - 30F - 20F +
                    0F +
                    0F +
                    100F

            childWidths shouldEqual listOf(
                    200F,
                    100F,
                    300F,
                    390F - 10F - 20F,
                    2F * 390F,
                    0F * 390F,
                    0F
            )

            childHeights shouldEqual listOf(
                    20F,
                    20F,
                    200F,
                    100F - 30F - 20F,
                    0F,
                    0F,
                    100F - 0F - 0F
            )

            childXs shouldEqual listOf(
                    50F,
                    50F,
                    50F,
                    10F,
                    50F,
                    0.1F * 390,
                    1F * 390
            )

            childYOffsets shouldEqual listOf(
                    30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 0F,
                    0F + 0F
            )
        }
    }

    @Test
    fun `render object with auto limited size in wrap space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = LayoutBox(
                autoSize(limits(0F, 100F)),
                Align.LEFT,
                listOf(
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(200F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(100F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(300F, 200F)),
                        Child(fixedMargins(10F, 20F, 30F, 20F), FillObject()),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(2F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(0F)),
                        Child(percentMargins(1F, 2F, 0.5F, 3F), FillObject())
                )
        )

        // when
        var renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 100F
            height shouldEqual 100F

            childWidths shouldEqual listOf(
                    200F,
                    100F,
                    300F,
                    100F - 10F - 20F,
                    2F * 100F,
                    0F * 100F,
                    0F
            )

            childHeights shouldEqual listOf(
                    20F,
                    20F,
                    200F,
                    100F - 30F - 20F,
                    0F,
                    0F,
                    100F - 0F - 0F
            )

            childXs shouldEqual listOf(
                    50F,
                    50F,
                    50F,
                    10F,
                    50F,
                    50F,
                    1F * 100
            )

            childYOffsets shouldEqual listOf(
                    30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 0F
            )
        }
    }

    @Test
    fun `render object with auto size in big wrap space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = LayoutBox(
                autoSize(),
                Align.LEFT,
                listOf(
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(200F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(100F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(300F, 200F)),
                        Child(fixedMargins(10F, 20F, 30F, 20F), FillObject()),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(2F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(0F)),
                        Child(percentMargins(1F, 2F, 0.5F, 3F), FillObject())
                )
        )

        // when
        var renderBox = layouter.layout(box, wrapSpace(1000F, 1000F))

        // then
        with (renderBox) {
            width shouldEqual 1000F
            height shouldEqual 6 * (20F + 30F) +
                    20F +
                    20F +
                    200F +
                    1000F - 30F - 20F +
                    0F +
                    0F +
                    1000F

            childWidths shouldEqual listOf(
                    200F,
                    100F,
                    300F,
                    1000F - 10F - 20F,
                    2F * 1000F,
                    0F * 1000F,
                    0F
            )

            childHeights shouldEqual listOf(
                    20F,
                    20F,
                    200F,
                    1000F - 30F - 20F,
                    0F,
                    0F,
                    1000F - 0F - 0F
            )

            childXs shouldEqual listOf(
                    50F,
                    50F,
                    50F,
                    10F,
                    50F,
                    50F,
                    1F * 1000
            )

            childYOffsets shouldEqual listOf(
                    30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 30F,
                    20F + 0F
            )
        }
    }

    @Test
    fun `render object with align right`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = LayoutBox(
                autoSize(),
                Align.RIGHT,
                listOf(
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(200F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(100F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(300F, 200F)),
                        Child(fixedMargins(10F, 20F, 30F, 20F), FillObject()),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(2F)),
                        Child(percentMargins(0.1F, 0.2F, 0F, 0F), PercentObject(0F)),
                        Child(percentMargins(1F, 2F, 0.5F, 3F), FillObject())
                )
        )

        // when
        var renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 300F + 50F + 40F
            height shouldEqual 5 * (20F + 30F) +
                    20F +
                    20F +
                    200F +
                    100F - 30F - 20F +
                    0F +
                    0F +
                    100F

            childWidths shouldEqual listOf(
                    200F,
                    100F,
                    300F,
                    390F - 10F - 20F,
                    2F * 390F,
                    0F * 390F,
                    0F
            )

            // boxWidth - marginRight - objWidth
            childXs shouldEqual listOf(
                    390F - 40F - 200F,
                    390F - 40F - 100F,
                    390F - 40F - 300F,
                    390F - 20F - 360F,
                    390F - 40F - 780F,
                    390F - 0.2F * 390F - 0F,
                    390F - 2F * 390F - 0F
            )
        }
    }

    @Test
    fun `render object with align center`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = LayoutBox(
                autoSize(),
                Align.RIGHT,
                listOf(
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(200F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(100F, 20F)),
                        Child(fixedMargins(50F, 40F, 30F, 20F), FixedObject(300F, 200F)),
                        Child(fixedMargins(10F, 20F, 30F, 20F), FillObject()),
                        Child(fixedMargins(50F, 40F, 30F, 20F), PercentObject(2F)),
                        Child(percentMargins(0.1F, 0.2F, 0F, 0F), PercentObject(0F)),
                        Child(percentMargins(1F, 2F, 0.5F, 3F), FillObject())
                )
        )

        // when
        var renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 300F + 50F + 40F
            height shouldEqual 5 * (20F + 30F) +
                    20F +
                    20F +
                    200F +
                    100F - 30F - 20F +
                    0F +
                    0F +
                    100F

            childWidths shouldEqual listOf(
                    200F,
                    100F,
                    300F,
                    390F - 10F - 20F,
                    2F * 390F,
                    0F * 390F,
                    0F
            )

            // marginLeft + (boxWidth - marginLeft - objWidth - marginRight) / 2
            childXs shouldEqual listOf(
                    50F + (390F - 50F - 200F - 40F),
                    50F + (390F - 50F - 100F - 40F),
                    50F + (390F - 50F - 300F - 40F),
                    10F + (390F - 10F - 360F - 20F),
                    50F + (390F - 50F - 780F - 40F),
                    0.1F * 390F + (390F - 0.1F * 390F - 0F - 0.2F * 390F),
                    1F * 390F + (390F - 1F * 390F - 0F - 2F * 390F)
            )
        }
    }

    @Test
    fun `render object with no children`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = LayoutBox(
                autoSize(),
                Align.LEFT,
                listOf()
        )

        // when
        var renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 0F
            height shouldEqual 0F
            children.size == 0
        }
    }

    fun childLayouter() = object : Layouter<LayoutObject, RenderObject> {
        override fun layout(obj: LayoutObject, space: LayoutSpace) = when (obj) {
            is FixedObject -> renderObject(
                    obj.width,
                    obj.height
            )
            is FillObject -> renderObject(
                    compute(space.width.area),
                    compute(space.height.area)
            )
            is PercentObject -> renderObject(
                    space.width.percentBase * obj.percent,
                    space.height.percentBase * obj.percent
            )
            else -> throw UnsupportedOperationException()
        }

        fun compute(area: LayoutSpace.Area) = when (area) {
            is LayoutSpace.Area.WrapContent -> area.max
            is LayoutSpace.Area.Fixed -> area.value
        }
    }

    fun fixedSize(width: Float, height: Float) = LayoutSize(
            LayoutSize.Dimension.Fixed(Absolute(width)),
            LayoutSize.Dimension.Fixed(Absolute(height))
    )

    fun autoSize(limits: LayoutSize.Limits = LayoutSize.Limits.NONE) = LayoutSize(
            LayoutSize.Dimension.Auto(limits),
            LayoutSize.Dimension.Auto(limits)
    )

    fun limits(min: Float, max: Float) = LayoutSize.Limits(Absolute(min), Absolute(max))

    fun fixedMargins(left: Float, right: Float, top: Float, bottom: Float) =
            LayoutBox.Margins(
                    Absolute(left),
                    Absolute(right),
                    Absolute(top),
                    Absolute(bottom)
            )

    fun percentMargins(left: Float, right: Float, top: Float, bottom: Float) =
            LayoutBox.Margins(
                    Percent(left),
                    Percent(right),
                    Percent(top),
                    Percent(bottom)
            )

    fun fixedSpace(width: Float, height: Float) = LayoutSpace(
            LayoutSpace.Dimension(width, LayoutSpace.Area.Fixed(width)),
            LayoutSpace.Dimension(height, LayoutSpace.Area.Fixed(height))
    )

    fun wrapSpace(maxWidth: Float, maxHeight: Float) = LayoutSpace(
            LayoutSpace.Dimension(0F, LayoutSpace.Area.WrapContent(maxWidth)),
            LayoutSpace.Dimension(0F, LayoutSpace.Area.WrapContent(maxHeight))
    )

    fun renderObject(width: Float, height: Float) = object : RenderObject(width, height, emptyList()) {
        override fun canPartiallyPainted() = false
    }

    val RenderObject.childWidths: List<Float>
        get() = children.map { it.obj.width }

    val RenderObject.childHeights: List<Float>
        get() = children.map { it.obj.height }

    val RenderObject.childXs: List<Float>
        get() = children.map { it.x }

    val RenderObject.childYOffsets: List<Float>
        get() = ArrayList<Float>().apply {
            var previewBottom = 0F
            for (child in children) {
                add(child.y - previewBottom)
                previewBottom = child.y + child.obj.height
            }
        }

    class FixedObject(val width: Float, val height: Float) : LayoutObject()
    class FillObject() : LayoutObject()
    class PercentObject(val percent: Float) : LayoutObject()
}