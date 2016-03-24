package com.dmi.perfectreader.layout.frame

import com.dmi.perfectreader.layout.LayoutFrame
import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.layout.common.LayoutSpace
import com.dmi.perfectreader.layout.common.LayoutSpace.Area
import com.dmi.perfectreader.layout.common.Layouter
import com.dmi.perfectreader.render.RenderChild
import com.dmi.perfectreader.render.RenderFrame
import com.dmi.perfectreader.render.RenderObject
import java.lang.Math.max

class FrameLayouter(
        private val childLayouter: Layouter<LayoutObject, RenderObject>
) : Layouter<LayoutFrame, RenderFrame> {
    override fun layout(obj: LayoutFrame, space: LayoutSpace): RenderFrame {
        return object {
            fun layout(): RenderFrame {
                val borderLeft = max(0F, obj.borders.left.width)
                val borderRight = max(0F, obj.borders.right.width)
                val borderTop = max(0F, obj.borders.top.width)
                val borderBottom = max(0F, obj.borders.bottom.width)

                val paddingLeft = max(0F, obj.paddings.left.compute(space.width.percentBase))
                val paddingRight = max(0F, obj.paddings.right.compute(space.width.percentBase))
                val paddingTop = max(0F, obj.paddings.top.compute(space.height.percentBase))
                val paddingBottom = max(0F, obj.paddings.bottom.compute(space.height.percentBase))

                val additionalWidth = borderLeft + borderRight + paddingLeft + paddingRight
                val additionalHeight = borderTop + borderBottom + paddingTop + paddingBottom

                val childSpace = LayoutSpace(
                        childDimension(space.width, additionalWidth),
                        childDimension(space.height, additionalHeight)
                )
                val renderObj = childLayouter.layout(obj.child, childSpace)

                return RenderFrame(
                        renderObj.width + additionalWidth,
                        renderObj.height + additionalHeight,
                        toRenderBorders(obj.borders),
                        toRenderBackground(obj.background),
                        RenderChild(borderLeft + paddingLeft, borderTop + paddingTop, renderObj)
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

            private fun toRenderBorders(borders: LayoutFrame.Borders) = RenderFrame.Borders(
                    toRenderBorder(borders.left),
                    toRenderBorder(borders.right),
                    toRenderBorder(borders.top),
                    toRenderBorder(borders.bottom)
            )

            private fun toRenderBorder(border: LayoutFrame.Border) = RenderFrame.Border(border.width, border.color)
            private fun toRenderBackground(background: LayoutFrame.Background) = RenderFrame.Background(background.color)
        }.layout()
    }
}
