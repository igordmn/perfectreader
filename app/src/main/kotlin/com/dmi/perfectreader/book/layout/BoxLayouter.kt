package com.dmi.perfectreader.book.layout

import com.dmi.perfectreader.book.content.common.Align
import com.dmi.perfectreader.book.content.configure.ConfiguredBox
import com.dmi.perfectreader.book.content.configure.ConfiguredObject
import com.dmi.perfectreader.book.content.configure.common.ConfiguredSize
import com.dmi.perfectreader.book.layout.common.LayoutSpace
import com.dmi.perfectreader.book.layout.common.LayoutSpace.Area
import com.dmi.perfectreader.book.layout.obj.LayoutBox
import com.dmi.perfectreader.book.layout.obj.LayoutChild
import com.dmi.perfectreader.book.layout.obj.LayoutObject
import java.util.*

class BoxLayouter(
        private val childLayouter: ObjectLayouter<ConfiguredObject, LayoutObject>
) : ObjectLayouter<ConfiguredBox, LayoutBox> {
    override fun layout(obj: ConfiguredBox, space: LayoutSpace): LayoutBox {
        val size = obj.size
        return object {
            fun layout(): LayoutBox {
                val width = computeWidth()

                val renderChildren = ArrayList<LayoutChild>()

                var y = 0F
                for (child in obj.children) {
                    val childSpace = childFixedSpace(width)

                    val layoutObj = childLayouter.layout(child, childSpace)

                    val x = childX(layoutObj.width, width)
                    renderChildren.add(LayoutChild(x, y, layoutObj))
                    y += layoutObj.height
                }

                return LayoutBox(width, computeHeight(y), renderChildren, obj.range)
            }

            fun computeWidth() = size.width.compute(space.width.percentBase, ::computeAutoWidth)
            fun computeHeight(wrapHeight: Float) = size.height.compute(space.height.percentBase) { wrapHeight }

            fun ConfiguredSize.Dimension.compute(percentBase: Float, getAutoValue: () -> Float) = when (this) {
                is ConfiguredSize.Dimension.Fixed -> compute(percentBase)
                is ConfiguredSize.Dimension.Auto -> compute(getAutoValue(), percentBase)
            }

            fun computeAutoWidth() = when (space.width.area) {
                is Area.Fixed -> space.width.area.value
                is Area.WrapContent -> computeWrapWidth(space.width.area.max)
            }

            fun computeWrapWidth(maxWidth: Float): Float {
                var width = 0F
                for (child in obj.children) {
                    val childSpace = childWrapSpace(maxWidth)
                    val layoutObj = childLayouter.layout(child, childSpace)
                    if (layoutObj.width > width)
                        width = layoutObj.width
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
                is ConfiguredSize.Dimension.Fixed -> childFixedDimension(
                        size.height.compute(space.height.percentBase)
                )
                is ConfiguredSize.Dimension.Auto -> childWrapDimension(
                        when (space.height.area) {
                            is Area.Fixed -> space.height.area.value
                            is Area.WrapContent -> space.height.area.max
                        }
                )
            }

            fun childFixedDimension(size: Float) = LayoutSpace.Dimension(size, Area.Fixed(size))
            fun childWrapDimension(maxSize: Float) = LayoutSpace.Dimension(maxSize, Area.WrapContent(maxSize))

            fun childX(objWidth: Float, boxWidth: Float) =
                when (obj.contentAlign) {
                    Align.LEFT -> 0F
                    Align.CENTER -> (boxWidth - objWidth) / 2
                    Align.RIGHT -> boxWidth - objWidth
                }
        }.layout()
    }
}