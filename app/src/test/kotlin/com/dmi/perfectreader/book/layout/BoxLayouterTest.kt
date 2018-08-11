package com.dmi.perfectreader.book.layout

import com.dmi.perfectreader.book.content.obj.ConfiguredBox
import com.dmi.perfectreader.book.content.obj.ConfiguredObject
import com.dmi.perfectreader.book.content.obj.param.Align
import com.dmi.perfectreader.book.content.obj.param.ConfiguredSize
import com.dmi.perfectreader.book.content.obj.param.Length.Absolute
import com.dmi.perfectreader.book.layout.common.LayoutSpace
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import com.dmi.perfectreader.layoutObj
import com.dmi.perfectreader.range
import com.dmi.test.shouldEqual
import org.junit.Test

@Suppress("IllegalIdentifier")
class BoxLayouterTest {
    @Test
    fun `render object with fixed size in fixed space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = ConfiguredBox(
                fixedSize(200F, 100F),
                Align.LEFT,
                listOf(
                        FixedObject(200F, 20F),
                        FixedObject(300F, 200F),
                        FillObject(),
                        PercentObject(2F)
                ),
                range()
        )

        // when
        val renderBox = layouter.layout(box, fixedSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 200F
            height shouldEqual 100F
            childWidths shouldEqual listOf(200F, 300F, 200F, 2F * 200F)
            childHeights shouldEqual listOf(20F, 200F, 100F, 2F * 100F)
            childXs shouldEqual listOf(0F, 0F, 0F, 0F)
            childYs shouldEqual listOf(0F, 20F, 20F + 200F, 220F + 100F)
            range shouldEqual box.range
        }
    }

    @Test
    fun `render object with fixed size in wrap space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = ConfiguredBox(
                fixedSize(200F, 100F),
                Align.LEFT,
                listOf(
                        FixedObject(200F, 20F),
                        FixedObject(300F, 200F),
                        FillObject(),
                        PercentObject(2F)
                ),
                range()
        )

        // when
        val renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 200F
            height shouldEqual 100F
            childWidths shouldEqual listOf(200F, 300F, 200F, 2F * 200F)
            childHeights shouldEqual listOf(20F, 200F, 100F, 2F * 100F)
        }
    }

    @Test
    fun `render object with auto size in fixed space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = ConfiguredBox(
                autoSize(),
                Align.LEFT,
                listOf(
                        FixedObject(200F, 20F),
                        FixedObject(300F, 200F),
                        FillObject(),
                        PercentObject(2F)
                ),
                range()
        )

        // when
        val renderBox = layouter.layout(box, fixedSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 100F
            height shouldEqual 20F + 200F + 100F + 2F * 0F
            childWidths shouldEqual listOf(200F, 300F, 100F, 2F * 100F)
            childHeights shouldEqual listOf(20F, 200F, 100F, 2F * 0F)
        }
    }

    @Test
    fun `render object with auto size in wrap space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = ConfiguredBox(
                autoSize(),
                Align.LEFT,
                listOf(
                        FixedObject(200F, 20F),
                        FixedObject(300F, 200F),
                        FillObject(),
                        PercentObject(2F)
                ),
                range()
        )

        // when
        val renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 300F
            height shouldEqual 20F + 200F + 100F + 2F * 0F
            childWidths shouldEqual listOf(200F, 300F, 300F, 2F * 300F)
            childHeights shouldEqual listOf(20F, 200F, 100F, 2F * 0F)
        }
    }

    @Test
    fun `render object with auto limited size in wrap space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = ConfiguredBox(
                autoSize(limits(0F, 100F)),
                Align.LEFT,
                listOf(
                        FixedObject(200F, 20F),
                        FixedObject(300F, 200F),
                        FillObject(),
                        PercentObject(2F)
                ),
                range()
        )

        // when
        val renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 100F
            height shouldEqual 100F
            childWidths shouldEqual listOf(200F, 300F, 100F, 2F * 100F)
            childHeights shouldEqual listOf(20F, 200F, 100F, 2F * 0F)
        }
    }

    @Test
    fun `render object with auto size in big wrap space`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = ConfiguredBox(
                autoSize(),
                Align.LEFT,
                listOf(
                        FixedObject(200F, 20F),
                        FixedObject(300F, 200F),
                        FillObject(),
                        PercentObject(2F)
                ),
                range()
        )

        // when
        val renderBox = layouter.layout(box, wrapSpace(1000F, 1000F))

        // then
        with (renderBox) {
            width shouldEqual 1000F
            height shouldEqual 20F + 200F + 1000F + 2F * 0F
            childWidths shouldEqual listOf(200F, 300F, 1000F, 2F * 1000F)
            childHeights shouldEqual listOf(20F, 200F, 1000F, 2F* 0F)
        }
    }

    @Test
    fun `render object with align right`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = ConfiguredBox(
                autoSize(),
                Align.RIGHT,
                listOf(
                        FixedObject(200F, 20F),
                        FixedObject(300F, 200F),
                        FillObject(),
                        PercentObject(2F)
                ),
                range()
        )

        // when
        val renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 300F
            childWidths shouldEqual listOf(200F, 300F, 300F, 2F * 300F)
            childXs shouldEqual listOf(
                    300F - 200F,
                    300F - 300F,
                    300F - 300F,
                    300F - 2F * 300F
            )
        }
    }

    @Test
    fun `render object with align center`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = ConfiguredBox(
                autoSize(),
                Align.CENTER,
                listOf(
                        FixedObject(200F, 20F),
                        FixedObject(300F, 200F),
                        FillObject(),
                        PercentObject(2F)
                ),
                range()
        )

        // when
        val renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 300F
            childWidths shouldEqual listOf(200F, 300F, 300F, 2F * 300F)
            childXs shouldEqual listOf(
                    (300F - 200F) / 2F,
                    (300F - 300F) / 2F,
                    (300F - 300F) / 2F,
                    (300F - 2F * 300F) / 2F
            )
        }
    }

    @Test
    fun `render object with no children`() {
        // given
        val layouter = BoxLayouter(childLayouter())
        val box = ConfiguredBox(
                autoSize(),
                Align.LEFT,
                listOf(),
                range()
        )

        // when
        val renderBox = layouter.layout(box, wrapSpace(100F, 100F))

        // then
        with (renderBox) {
            width shouldEqual 0F
            height shouldEqual 0F
            children.size == 0
        }
    }

    fun childLayouter() = object : ObjectLayouter<ConfiguredObject, LayoutObject> {
        override fun layout(obj: ConfiguredObject, space: LayoutSpace) = when (obj) {
            is FixedObject -> layoutObj(
                    obj.width,
                    obj.height
            )
            is FillObject -> layoutObj(
                    compute(space.width.area),
                    compute(space.height.area)
            )
            is PercentObject -> layoutObj(
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

    fun fixedSize(width: Float, height: Float) = ConfiguredSize(
            ConfiguredSize.Dimension.Fixed(Absolute(width)),
            ConfiguredSize.Dimension.Fixed(Absolute(height))
    )

    fun autoSize(limits: ConfiguredSize.Limits = ConfiguredSize.Limits.NONE) = ConfiguredSize(
            ConfiguredSize.Dimension.Auto(limits),
            ConfiguredSize.Dimension.Auto(limits)
    )

    fun limits(min: Float, max: Float) = ConfiguredSize.Limits(Absolute(min), Absolute(max))

    fun fixedSpace(width: Float, height: Float) = LayoutSpace(
            LayoutSpace.Dimension(width, LayoutSpace.Area.Fixed(width)),
            LayoutSpace.Dimension(height, LayoutSpace.Area.Fixed(height))
    )

    fun wrapSpace(maxWidth: Float, maxHeight: Float) = LayoutSpace(
            LayoutSpace.Dimension(0F, LayoutSpace.Area.WrapContent(maxWidth)),
            LayoutSpace.Dimension(0F, LayoutSpace.Area.WrapContent(maxHeight))
    )

    val LayoutObject.childWidths: List<Float>
        get() = children.map { it.obj.width }

    val LayoutObject.childHeights: List<Float>
        get() = children.map { it.obj.height }

    val LayoutObject.childXs: List<Float>
        get() = children.map { it.x }

    val LayoutObject.childYs: List<Float>
        get() = children.map { it.y }

    inner class FixedObject(val width: Float, val height: Float) : ConfiguredObject(range())
    inner class FillObject() : ConfiguredObject(range())
    inner class PercentObject(val percent: Float) : ConfiguredObject(range())
}