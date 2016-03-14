package com.dmi.perfectreader.layout.config

import com.dmi.util.lang.clamp

class LayoutSize(val width: LimitedValue, val height: LimitedValue) {
    inline fun computeWidth(context: LayoutContext, wrappedWidth: () -> Float) =
            width.compute(context.parentSize.width, context.areaSize.width, wrappedWidth)

    inline fun computeHeight(context: LayoutContext, wrappedHeight: () -> Float) =
            height.compute(context.parentSize.height, context.areaSize.height, wrappedHeight)

    class LimitedValue(val value: Value, val min: Limit, val max: Limit) {
        inline fun compute(parentSize: Float, areaSize: Float, wrapped: () -> Float): Float {
            return clamp(
                    when (min) {
                        is Limit.None -> 0F
                        is Limit.Absolute -> min.value
                        is Limit.ParentPercent -> parentSize * min.percent
                    },
                    when (max) {
                        is Limit.None -> Float.MAX_VALUE
                        is Limit.Absolute -> max.value
                        is Limit.ParentPercent -> parentSize * max.percent
                    },
                    when (value) {
                        is Value.Absolute -> value.value
                        is Value.ParentPercent -> parentSize * value.percent
                        is Value.FillArea -> areaSize
                        is Value.WrapContent -> wrapped()
                    }
            )
        }
    }

    sealed class Value {
        class Absolute(val value: Float) : Value()
        class ParentPercent(val percent: Float) : Value()
        class FillArea : Value()
        class WrapContent : Value()
    }

    sealed class Limit {
        class None: Limit()
        class Absolute(val value: Float) : Limit()
        class ParentPercent(val percent: Float) : Limit()
    }
}
