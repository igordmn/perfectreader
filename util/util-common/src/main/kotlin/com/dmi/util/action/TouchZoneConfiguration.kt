package com.dmi.util.action

import com.dmi.util.action.TouchZoneConfiguration.Shapes.Zone
import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.unsupported

private const val FIXED_WIDTH = 24  // in dips

private val singleLines = listOf(Zone(-1, TouchZone1D.MIDDLE1))
private val twoLines = listOf(Zone(-1, TouchZone1D.FIRST), Zone(-1, TouchZone1D.LAST))
private val threeLines = listOf(Zone(-1, TouchZone1D.FIRST), Zone(-1, TouchZone1D.MIDDLE1), Zone(-1, TouchZone1D.LAST))
private val threeFixedLines = listOf(Zone(FIXED_WIDTH, TouchZone1D.FIRST), Zone(-1, TouchZone1D.MIDDLE1), Zone(FIXED_WIDTH, TouchZone1D.LAST))
private val fourFixedLines = listOf(Zone(FIXED_WIDTH, TouchZone1D.FIRST), Zone(-1, TouchZone1D.MIDDLE1), Zone(-1, TouchZone1D.MIDDLE2), Zone(FIXED_WIDTH, TouchZone1D.LAST))
        
enum class TouchZoneConfiguration {
    /**
     *  _ _ _ _ _ _
     * |           |
     * |           |
     * |           |
     * |           |
     * |           |
     * |_ _ _ _ _ _|
     */
    SINGLE {
        override val shapes = Shapes.Table(singleLines, singleLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(single(), single())
    },

    /**
     *  _ _ _ _ _ _
     * |     |     |
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |     |     |
     * |_ _ _|_ _ _|
     */
    FOUR {
        override val shapes = Shapes.Table(twoLines, twoLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(two(position.x, size.width), two(position.y, size.height))
    },

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    NINE {
        override val shapes = Shapes.Table(threeLines, threeLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(three(position.x, size.width), three(position.y, size.height))
    },

    /**
     *  _ _ _ _ _ _
     * |_|_ _|_ _|_|
     * | |   |   | |
     * |_|_ _|_ _|_|
     * | |   |   | |
     * |_|_ _|_ _|_|
     * |_|_ _|_ _|_|
     */
    SIXTEEN_FIXED {
        override val shapes = Shapes.Table(fourFixedLines, fourFixedLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(fourFixed(position.x, size.width), fourFixed(position.y, size.height))
    },

    /**
     *  _ _ _ _ _ _
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |_ _ _|_ _ _|
     */
    THREE_ROWS_TWO_COLUMNS {
        override val shapes = Shapes.Table(twoLines, threeLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(two(position.x, size.width), three(position.y, size.height))
    },

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    TWO_ROWS_THREE_COLUMNS {
        override val shapes = Shapes.Table(threeLines, twoLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(three(position.x, size.width), two(position.y, size.height))
    },

    /**
     *  _ _ _ _ _ _
     * |           |
     * |           |
     * |_ _ _ _ _ _|
     * |           |
     * |           |
     * |_ _ _ _ _ _|
     */
    TWO_ROWS {
        override val shapes = Shapes.Table(singleLines, twoLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(single(), two(position.y, size.height))
    },

    /**
     *  _ _ _ _ _ _
     * |           |
     * |_ _ _ _ _ _|
     * |           |
     * |_ _ _ _ _ _|
     * |           |
     * |_ _ _ _ _ _|
     */
    THREE_ROWS {
        override val shapes = Shapes.Table(singleLines, threeLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(single(), three(position.y, size.height))
    },

    /**
     *  _ _ _ _ _ _
     * |_ _ _ _ _ _|
     * |           |
     * |           |
     * |           |
     * |_ _ _ _ _ _|
     * |_ _ _ _ _ _|
     */
    THREE_ROWS_FIXED {
        override val shapes = Shapes.Table(singleLines, threeFixedLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(single(), threeFixed(position.y, size.height))
    },

    /**
     *  _ _ _ _ _ _
     * |_ _ _ _ _ _|
     * |           |
     * |_ _ _ _ _ _|
     * |           |
     * |_ _ _ _ _ _|
     * |_ _ _ _ _ _|
     */
    FOUR_ROWS_FIXED {
        override val shapes = Shapes.Table(singleLines, fourFixedLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(single(), fourFixed(position.y, size.height))
    },

    /**
     *  _ _ _ _ _ _
     * |     |     |
     * |     |     |
     * |     |     |
     * |     |     |
     * |     |     |
     * |_ _ _|_ _ _|
     */
    TWO_COLUMNS {
        override val shapes = Shapes.Table(twoLines, singleLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(two(position.x, size.width), single())
    },

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |   |   |   |
     * |   |   |   |
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    THREE_COLUMNS {
        override val shapes = Shapes.Table(threeLines, singleLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(three(position.x, size.width), single())
    },

    /**
     *  _ _ _ _ _ _
     * | |       | |
     * | |       | |
     * | |       | |
     * | |       | |
     * | |       | |
     * |_|_ _ _ _|_|
     */
    THREE_COLUMNS_FIXED {
        override val shapes = Shapes.Table(threeFixedLines, singleLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(threeFixed(position.x, size.width), single())
    },

    /**
     *  _ _ _ _ _ _
     * | |   |   | |
     * | |   |   | |
     * | |   |   | |
     * | |   |   | |
     * | |   |   | |
     * |_|_ _|_ _|_|
     */
    FOUR_COLUMNS_FIXED {
        override val shapes = Shapes.Table(fourFixedLines, singleLines)
        override fun Touches.get(position: PositionF, size: SizeF) = zone(fourFixed(position.x, size.width), single())
    },

    /**
     *  _ _ _ _
     * |\     /|
     * | \   / |
     * |  \ /  |
     * |  / \  |
     * | /   \ |
     * |/_ _ _\|
     */
    TRIANGLE_SIDES {
        override val shapes: Shapes get() = unsupported()
        override fun Touches.get(position: PositionF, size: SizeF) = triangles(position, size)
    },

    /**
     *  _ _ _ _ _
     * |\       /|
     * | \ _ _ / |
     * |  |   |  |
     * |  |_ _|  |
     * | /     \ |
     * |/_ _ _ _\|
     */
    TRIANGLE_SIDES_CENTER {
        override val shapes: Shapes get() = unsupported()

        override fun Touches.get(position: PositionF, size: SizeF) = when {
            isCenter(position, size) -> TouchZone.MIDDLE1_MIDDLE1
            else -> triangles(position, size)
        }
    };

    abstract val shapes: Shapes
    
    /**
     * [position] in dips
     * [size] in dips
     */
    operator fun get(position: PositionF, size: SizeF): TouchZone = with(Touches) { get(position, size) }

    protected abstract fun Touches.get(position: PositionF, size: SizeF): TouchZone

    protected object Touches {
        fun single() = TouchZone1D.MIDDLE1

        fun two(value: Float, max: Float) = when {
            value / max < 1 / 2F -> TouchZone1D.FIRST
            else -> TouchZone1D.LAST
        }

        fun three(value: Float, max: Float) = when {
            value / max < 1 / 3F -> TouchZone1D.FIRST
            value / max > 2 / 3F -> TouchZone1D.LAST
            else -> TouchZone1D.MIDDLE1
        }

        fun threeFixed(value: Float, max: Float) = when {
            value < FIXED_WIDTH -> TouchZone1D.FIRST
            value > max - FIXED_WIDTH -> TouchZone1D.LAST
            else -> TouchZone1D.MIDDLE1
        }

        fun fourFixed(value: Float, max: Float) = when {
            value < FIXED_WIDTH -> TouchZone1D.FIRST
            value > max - FIXED_WIDTH -> TouchZone1D.LAST
            value / max < 2 / 4F -> TouchZone1D.MIDDLE1
            else -> TouchZone1D.MIDDLE2
        }

        fun triangles(position: PositionF, size: SizeF): TouchZone {
            val xpart = position.x / size.width
            val ypart = position.y / size.height

            return when {
                ypart < 1 - xpart -> when {
                    ypart > xpart -> TouchZone.MIDDLE1_LEFT
                    else -> TouchZone.TOP_MIDDLE1
                }
                else -> when {
                    ypart > xpart -> TouchZone.MIDDLE1_RIGHT
                    else -> TouchZone.BOTTOM_MIDDLE1
                }
            }
        }

        fun isCenter(position: PositionF, size: SizeF): Boolean {
            val xpart = position.x / size.width
            val ypart = position.y / size.height
            return xpart >= 1 / 3F && xpart <= 2 / 3F && ypart >= 1 / 3F && ypart <= 2 / 3F
        }
    }

    // todo triangles
    sealed class Shapes {
        class Table(val horizontals: List<Zone>, val verticals: List<Zone>) : Shapes()

        /**
         * [size] -1 for match_parent or fixed value
         */
        class Zone(val size: Int, val zone: TouchZone1D)
    }
}