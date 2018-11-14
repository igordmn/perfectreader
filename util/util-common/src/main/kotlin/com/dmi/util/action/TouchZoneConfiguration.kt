package com.dmi.util.action

import com.dmi.util.graphic.PositionF
import com.dmi.util.graphic.SizeF
import com.dmi.util.lang.unsupported

private const val FIXED_WIDTH = 24  // in dips

private val singleLines = listOf(-1)
private val twoLines = listOf(-1, -1)
private val threeLines = listOf(-1, -1, -1)
private val threeFixedLines = listOf(FIXED_WIDTH, -1, FIXED_WIDTH)
private val fourFixedLines = listOf(FIXED_WIDTH, -1, -1, FIXED_WIDTH)
        
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(single(), single())
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(two(position.x, size.width), two(position.y, size.height))
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(three(position.x, size.width), three(position.y, size.height))
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(fourFixed(position.x, size.width), fourFixed(position.y, size.height))
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(two(position.x, size.width), three(position.y, size.height))
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(three(position.x, size.width), two(position.y, size.height))
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(single(), two(position.y, size.height))
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(single(), three(position.y, size.height))
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(single(), threeFixed(position.y, size.height))
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(single(), fourFixed(position.y, size.height))
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(two(position.x, size.width), single())
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(three(position.x, size.width), single())
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(threeFixed(position.x, size.width), single())
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
        override fun Touches.get(position: PositionF, size: SizeF) = rectangles(fourFixed(position.x, size.width), single())
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

        fun rectangles(column: TouchZone1D, row: TouchZone1D): TouchZone = when {
            row == TouchZone1D.FIRST && column == TouchZone1D.FIRST -> TouchZone.TOP_LEFT
            row == TouchZone1D.FIRST && column == TouchZone1D.MIDDLE1 -> TouchZone.TOP_MIDDLE1
            row == TouchZone1D.FIRST && column == TouchZone1D.MIDDLE2 -> TouchZone.TOP_MIDDLE2
            row == TouchZone1D.FIRST && column == TouchZone1D.LAST -> TouchZone.TOP_RIGHT
            row == TouchZone1D.MIDDLE1 && column == TouchZone1D.FIRST -> TouchZone.MIDDLE1_LEFT
            row == TouchZone1D.MIDDLE1 && column == TouchZone1D.MIDDLE1 -> TouchZone.MIDDLE1_MIDDLE1
            row == TouchZone1D.MIDDLE1 && column == TouchZone1D.MIDDLE2 -> TouchZone.MIDDLE1_MIDDLE2
            row == TouchZone1D.MIDDLE1 && column == TouchZone1D.LAST -> TouchZone.MIDDLE1_RIGHT
            row == TouchZone1D.MIDDLE2 && column == TouchZone1D.FIRST -> TouchZone.MIDDLE2_LEFT
            row == TouchZone1D.MIDDLE2 && column == TouchZone1D.MIDDLE1 -> TouchZone.MIDDLE2_MIDDLE1
            row == TouchZone1D.MIDDLE2 && column == TouchZone1D.MIDDLE2 -> TouchZone.MIDDLE2_MIDDLE2
            row == TouchZone1D.MIDDLE2 && column == TouchZone1D.LAST -> TouchZone.MIDDLE2_RIGHT
            row == TouchZone1D.LAST && column == TouchZone1D.FIRST -> TouchZone.BOTTOM_LEFT
            row == TouchZone1D.LAST && column == TouchZone1D.MIDDLE1 -> TouchZone.BOTTOM_MIDDLE1
            row == TouchZone1D.LAST && column == TouchZone1D.MIDDLE2 -> TouchZone.BOTTOM_MIDDLE2
            else -> TouchZone.BOTTOM_RIGHT
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
        class Table(val widths: List<Int>, val heights: List<Int>) : Shapes()
    }
}