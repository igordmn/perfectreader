package com.dmi.perfectreader.fragment.book.layout.layouter

import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace.Area
import com.dmi.perfectreader.fragment.book.obj.content.ComputedFrame
import com.dmi.perfectreader.fragment.book.obj.content.ComputedObject
import com.dmi.perfectreader.fragment.book.obj.render.RenderChild
import com.dmi.perfectreader.fragment.book.obj.render.RenderFrame
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject
import java.lang.Math.max

class FrameLayouter(
        private val childLayouter: Layouter<ComputedObject, RenderObject>
) : Layouter<ComputedFrame, RenderFrame> {
    override fun layout(obj: ComputedFrame, space: LayoutSpace): RenderFrame {
        return object {
            fun layout(): RenderFrame {
                val marginLeft = obj.margins.left.compute(space.width.percentBase)
                val marginRight = obj.margins.right.compute(space.width.percentBase)
                val marginTop = obj.margins.top.compute(space.height.percentBase)
                val marginBottom = obj.margins.bottom.compute(space.height.percentBase)

                val borderLeft = obj.borders.left.width
                val borderRight = obj.borders.right.width
                val borderTop = obj.borders.top.width
                val borderBottom = obj.borders.bottom.width

                val paddingLeft = obj.paddings.left.compute(space.width.percentBase)
                val paddingRight = obj.paddings.right.compute(space.width.percentBase)
                val paddingTop = obj.paddings.top.compute(space.height.percentBase)
                val paddingBottom = obj.paddings.bottom.compute(space.height.percentBase)

                val additionalWidth = marginLeft + marginRight + borderLeft + borderRight + paddingLeft + paddingRight
                val additionalHeight = marginTop + marginBottom + borderTop + borderBottom + paddingTop + paddingBottom

                val childSpace = LayoutSpace(
                        childDimension(space.width, additionalWidth),
                        childDimension(space.height, additionalHeight)
                )
                val renderObj = childLayouter.layout(obj.child, childSpace)

                return RenderFrame(
                        renderObj.width + additionalWidth,
                        renderObj.height + additionalHeight,
                        RenderObject.Margins(marginLeft, marginRight, marginTop, marginBottom),
                        toRenderBorders(obj.borders),
                        toRenderBackground(obj.background),
                        RenderChild(marginLeft + borderLeft + paddingLeft, marginTop + borderTop + paddingTop, renderObj),
                        obj.range
                )
            }

            private fun childDimension(dimension: LayoutSpace.Dimension, subtractedWidth: Float) =
                    LayoutSpace.Dimension(
                            dimension.percentBase,
                            childArea(dimension.area, subtractedWidth)
                    )

            private fun childArea(area: Area, subtractedWidth: Float) =
                    when (area) {
                        is Area.WrapContent -> Area.WrapContent(
                                max(0F, area.max - subtractedWidth)
                        )
                        is Area.Fixed -> Area.Fixed(
                                max(0F, area.value - subtractedWidth)
                        )
                    }

            private fun toRenderBorders(borders: ComputedFrame.Borders) = RenderFrame.Borders(
                    toRenderBorder(borders.left),
                    toRenderBorder(borders.right),
                    toRenderBorder(borders.top),
                    toRenderBorder(borders.bottom)
            )

            private fun toRenderBorder(border: ComputedFrame.Border) = RenderFrame.Border(border.width, border.color)
            private fun toRenderBackground(background: ComputedFrame.Background) = RenderFrame.Background(background.color)
        }.layout()
    }
}