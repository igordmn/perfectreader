package com.dmi.perfectreader.fragment.book.layout.layouter

import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace.Area
import com.dmi.perfectreader.fragment.book.obj.content.ComputedFrame
import com.dmi.perfectreader.fragment.book.obj.content.ComputedObject
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutChild
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutFrame
import com.dmi.perfectreader.fragment.book.obj.layout.LayoutObject
import java.lang.Math.max

class FrameLayouter(
        private val childLayouter: Layouter<ComputedObject, LayoutObject>
) : Layouter<ComputedFrame, LayoutFrame> {
    override fun layout(obj: ComputedFrame, space: LayoutSpace): LayoutFrame {
        return object {
            fun layout(): LayoutFrame {
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
                val layoutObj = childLayouter.layout(obj.child, childSpace)

                return LayoutFrame(
                        layoutObj.width + additionalWidth,
                        layoutObj.height + additionalHeight,
                        LayoutObject.Margins(marginLeft, marginRight, marginTop, marginBottom),
                        toLayoutBorders(obj.borders),
                        toLayoutBackground(obj.background),
                        LayoutChild(marginLeft + borderLeft + paddingLeft, marginTop + borderTop + paddingTop, layoutObj),
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

            private fun toLayoutBorders(borders: ComputedFrame.Borders) = LayoutFrame.Borders(
                    toLayoutBorder(borders.left),
                    toLayoutBorder(borders.right),
                    toLayoutBorder(borders.top),
                    toLayoutBorder(borders.bottom)
            )

            private fun toLayoutBorder(border: ComputedFrame.Border) = LayoutFrame.Border(border.width, border.color)
            private fun toLayoutBackground(background: ComputedFrame.Background) = LayoutFrame.Background(background.color)
        }.layout()
    }
}