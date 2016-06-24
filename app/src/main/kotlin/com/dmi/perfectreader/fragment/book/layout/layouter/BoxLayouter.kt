package com.dmi.perfectreader.fragment.book.layout.layouter

import com.dmi.perfectreader.fragment.book.obj.content.param.ComputedSize
import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace
import com.dmi.perfectreader.fragment.book.layout.layouter.common.LayoutSpace.Area
import com.dmi.perfectreader.fragment.book.obj.content.ComputedBox
import com.dmi.perfectreader.fragment.book.obj.content.ComputedObject
import com.dmi.perfectreader.fragment.book.obj.render.RenderBox
import com.dmi.perfectreader.fragment.book.obj.render.RenderChild
import com.dmi.perfectreader.fragment.book.obj.render.RenderObject
import com.dmi.perfectreader.fragment.book.obj.common.Align
import java.util.*

class BoxLayouter(
        private val childLayouter: Layouter<ComputedObject, RenderObject>
) : Layouter<ComputedBox, RenderBox> {
    override fun layout(obj: ComputedBox, space: LayoutSpace): RenderBox {
        val size = obj.size
        return object {
            fun layout(): RenderBox {
                val width = computeWidth()

                val renderChildren = ArrayList<RenderChild>()

                var y = 0F
                for (child in obj.children) {
                    val childSpace = childFixedSpace(width)

                    val renderObj = childLayouter.layout(child, childSpace)

                    val x = childX(renderObj.width, width)
                    renderChildren.add(RenderChild(x, y, renderObj))
                    y += renderObj.height
                }

                return RenderBox(width, computeHeight(y), renderChildren, obj.range)
            }

            fun computeWidth() = size.width.compute(space.width.percentBase, { computeAutoWidth() })
            fun computeHeight(wrapHeight: Float) = size.height.compute(space.height.percentBase, { wrapHeight })

            fun ComputedSize.Dimension.compute(percentBase: Float, getAutoValue: () -> Float) = when (this) {
                is ComputedSize.Dimension.Fixed -> compute(percentBase)
                is ComputedSize.Dimension.Auto -> compute(getAutoValue(), percentBase)
            }

            fun computeAutoWidth() = when (space.width.area) {
                is Area.Fixed -> space.width.area.value
                is Area.WrapContent -> computeWrapWidth(space.width.area.max)
            }

            fun computeWrapWidth(maxWidth: Float): Float {
                var width = 0F
                for (child in obj.children) {
                    val childSpace = childWrapSpace(maxWidth)
                    val renderObj = childLayouter.layout(child, childSpace)
                    if (renderObj.width > width)
                        width = renderObj.width
                }
                return width
            }

            fun childFixedSpace(width: Float) = LayoutSpace(
                    childFixedDimension(width),
                    childHeightDimension()
            )

            fun childWrapSpace(maxWidth: Float) = LayoutSpace(
                    childWrapDimension(maxWidth),
                    childHeightDimension()
            )

            fun childHeightDimension() = when (size.height) {
                is ComputedSize.Dimension.Fixed -> childFixedDimension(
                        size.height.compute(space.height.percentBase)
                )
                is ComputedSize.Dimension.Auto -> childWrapDimension(
                        when (space.height.area) {
                            is Area.Fixed -> space.height.area.value
                            is Area.WrapContent -> space.height.area.max
                        }
                )
            }

            fun childFixedDimension(size: Float) = LayoutSpace.Dimension(size, Area.Fixed(size))
            fun childWrapDimension(maxSize: Float) = LayoutSpace.Dimension(0F, Area.WrapContent(maxSize))

            fun childX(objWidth: Float, boxWidth: Float) =
                when (obj.contentAlign) {
                    Align.LEFT -> 0F
                    Align.CENTER -> (boxWidth - objWidth) / 2
                    Align.RIGHT -> boxWidth - objWidth
                }
        }.layout()
    }
}