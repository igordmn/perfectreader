package com.dmi.perfectreader.layout.box

import com.dmi.perfectreader.layout.LayoutBox
import com.dmi.perfectreader.layout.LayoutObject
import com.dmi.perfectreader.layout.common.LayoutLength
import com.dmi.perfectreader.layout.common.LayoutSize
import com.dmi.perfectreader.layout.common.LayoutSpace
import com.dmi.perfectreader.layout.common.LayoutSpace.Area
import com.dmi.perfectreader.layout.common.Layouter
import com.dmi.perfectreader.render.RenderBox
import com.dmi.perfectreader.render.RenderChild
import com.dmi.perfectreader.render.RenderObject
import com.dmi.perfectreader.style.Align
import java.lang.Math.max
import java.util.*

class BoxLayouter(
        private val childLayouter: Layouter<LayoutObject, RenderObject>
) : Layouter<LayoutBox, RenderBox> {
    override fun layout(obj: LayoutBox, space: LayoutSpace): RenderBox {
        val size = obj.size
        return object {
            fun layout(): RenderBox {
                val width = computeWidth()

                val renderChildren = ArrayList<RenderChild>()

                var y = 0F
                for (child in obj.children) {
                    val childSpace = childFixedSpace(child, width)

                    val marginLeft = child.margins.left.compute(childSpace.width.percentBase)
                    val marginRight = child.margins.right.compute(childSpace.width.percentBase)
                    val marginTop = child.margins.top.compute(childSpace.height.percentBase)
                    val marginBottom = child.margins.bottom.compute(childSpace.height.percentBase)

                    val renderObj = childLayouter.layout(child.obj, childSpace)

                    val x = childX(renderObj.width, width, marginLeft, marginRight)
                    y += marginTop
                    renderChildren.add(RenderChild(x, y, renderObj))
                    y += renderObj.height
                    y += marginBottom
                }

                return RenderBox(width, computeHeight(y), renderChildren)
            }

            fun computeWidth() = size.width.compute(space.width.percentBase, { computeAutoWidth() })
            fun computeHeight(wrapHeight: Float) = size.height.compute(space.height.percentBase, { wrapHeight })

            fun LayoutSize.Dimension.compute(percentBase: Float, getAutoValue: () -> Float) = when (this) {
                is LayoutSize.Dimension.Fixed -> compute(percentBase)
                is LayoutSize.Dimension.Auto -> compute(getAutoValue(), percentBase)
            }

            fun computeAutoWidth() = when (space.width.area) {
                is Area.Fixed -> space.width.area.value
                is Area.WrapContent -> computeWrapWidth(space.width.area.max)
            }

            fun computeWrapWidth(maxWidth: Float): Float {
                var width = 0F

                for (child in obj.children) {
                    val childSpace = childWrapSpace(child, maxWidth)

                    val marginLeft = child.margins.left.compute(childSpace.width.percentBase)
                    val marginRight = child.margins.right.compute(childSpace.height.percentBase)

                    val renderObj = childLayouter.layout(child.obj, childSpace)

                    val childWidth = marginLeft + renderObj.width + marginRight
                    if (childWidth > width)
                        width = childWidth
                }

                return width
            }

            fun childFixedSpace(child: LayoutBox.Child, width: Float) = LayoutSpace(
                    childFixedDimension(width, child.margins.left, child.margins.right),
                    childHeightDimension(child.margins.top, child.margins.bottom)
            )

            fun childWrapSpace(child: LayoutBox.Child, maxWidth: Float) = LayoutSpace(
                    childWrapDimension(maxWidth, child.margins.left, child.margins.right),
                    childHeightDimension(child.margins.top, child.margins.bottom)
            )

            fun childHeightDimension(margin1: LayoutLength, margin2: LayoutLength) = when (size.height) {
                is LayoutSize.Dimension.Fixed -> childFixedDimension(
                        size.height.compute(space.height.percentBase),
                        margin1,
                        margin2
                )
                is LayoutSize.Dimension.Auto -> childWrapDimension(
                        when (space.height.area) {
                            is Area.Fixed -> space.height.area.value
                            is Area.WrapContent -> space.height.area.max
                        },
                        margin1,
                        margin2
                )
            }

            fun childFixedDimension(size: Float, margin1: LayoutLength, margin2: LayoutLength) = LayoutSpace.Dimension(
                    size,
                    Area.Fixed(
                            max(0F, size - margin1.compute(size) - margin2.compute(size))
                    )
            )

            fun childWrapDimension(maxSize: Float, margin1: LayoutLength, margin2: LayoutLength) = LayoutSpace.Dimension(
                    0F,
                    Area.WrapContent(
                            max(0F, maxSize - margin1.compute(0F) - margin2.compute(0F))
                    )
            )

            fun childX(objWidth: Float, areaWidth: Float, marginLeft: Float, marginRight: Float) =
                when (obj.contentAlign) {
                    Align.LEFT -> marginLeft
                    Align.CENTER -> marginLeft + (areaWidth - marginLeft - objWidth - marginRight) / 2
                    Align.RIGHT -> areaWidth - marginRight - objWidth
                }
        }.layout()
    }
}
