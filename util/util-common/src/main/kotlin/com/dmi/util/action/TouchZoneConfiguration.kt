package com.dmi.util.action

private val FIXED_LINE_WIDTH = 24

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
    SINGLE,

    /**
     *  _ _ _ _ _ _
     * |     |     |
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |     |     |
     * |_ _ _|_ _ _|
     */
    FOUR,

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    NINE,

    /**
     *  _ _ _ _ _ _
     * |_|_ _|_ _|_|
     * | |   |   | |
     * |_|_ _|_ _|_|
     * | |   |   | |
     * |_|_ _|_ _|_|
     * |_|_ _|_ _|_|
     */
    SIXTEEN_FIXED,

    /**
     *  _ _ _ _ _ _
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |_ _ _|_ _ _|
     */
    THREE_ROWS_TWO_COLUMNS,

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    TWO_ROWS_THREE_COLUMNS,

    /**
     *  _ _ _ _ _ _
     * |           |
     * |           |
     * |_ _ _ _ _ _|
     * |           |
     * |           |
     * |_ _ _ _ _ _|
     */
    TWO_ROWS,

    /**
     *  _ _ _ _ _ _
     * |           |
     * |_ _ _ _ _ _|
     * |           |
     * |_ _ _ _ _ _|
     * |           |
     * |_ _ _ _ _ _|
     */
    THREE_ROWS,

    /**
     *  _ _ _ _ _ _
     * |_ _ _ _ _ _|
     * |           |
     * |           |
     * |           |
     * |_ _ _ _ _ _|
     * |_ _ _ _ _ _|
     */
    THREE_ROWS_FIXED,

    /**
     *  _ _ _ _ _ _
     * |_ _ _ _ _ _|
     * |           |
     * |_ _ _ _ _ _|
     * |           |
     * |_ _ _ _ _ _|
     * |_ _ _ _ _ _|
     */
    FOUR_ROWS_FIXED,

    /**
     *  _ _ _ _ _ _
     * |     |     |
     * |     |     |
     * |     |     |
     * |     |     |
     * |     |     |
     * |_ _ _|_ _ _|
     */
    TWO_COLUMNS,

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |   |   |   |
     * |   |   |   |
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    THREE_COLUMNS,

    /**
     *  _ _ _ _ _ _
     * | |       | |
     * | |       | |
     * | |       | |
     * | |       | |
     * | |       | |
     * |_|_ _ _ _|_|
     */
    THREE_COLUMNS_FIXED,

    /**
     *  _ _ _ _ _ _
     * | |   |   | |
     * | |   |   | |
     * | |   |   | |
     * | |   |   | |
     * | |   |   | |
     * |_|_ _|_ _|_|
     */
    FOUR_COLUMNS_FIXED,

    /**
     *  _ _ _ _
     * |\     /|
     * | \   / |
     * |  \ /  |
     * |  / \  |
     * | /   \ |
     * |/_ _ _\|
     */
    TRIANGLE_SIDES,

    /**
     *  _ _ _ _ _
     * |\       /|
     * | \ _ _ / |
     * |  |   |  |
     * |  |_ _|  |
     * | /     \ |
     * |/_ _ _ _\|
     */
    TRIANGLE_SIDES_CENTER;

    fun getAt(x: Float, y: Float, width: Float, height: Float): TouchZone = when (this) {
        SINGLE -> squares(single(), single())
        FOUR -> squares(two(x, width), two(y, height))
        NINE -> squares(three(x, width), three(y, height))
        SIXTEEN_FIXED -> squares(fourFixed(x, width), fourFixed(y, height))
        THREE_ROWS_TWO_COLUMNS -> squares(two(x, width), three(y, height))
        TWO_ROWS_THREE_COLUMNS -> squares(three(x, width), two(y, height))
        TWO_ROWS -> squares(single(), two(y, height))
        THREE_ROWS -> squares(single(), three(y, height))
        THREE_ROWS_FIXED -> squares(single(), threeFixed(y, height))
        FOUR_ROWS_FIXED -> squares(single(), fourFixed(y, height))
        TWO_COLUMNS -> squares(two(x, width), single())
        THREE_COLUMNS -> squares(three(x, width), single())
        THREE_COLUMNS_FIXED -> squares(threeFixed(x, width), single())
        FOUR_COLUMNS_FIXED -> squares(fourFixed(x, width), single())
        TRIANGLE_SIDES -> triangles(x, y, width, height)
        TRIANGLE_SIDES_CENTER -> when {
            isCenter(x, y, width, height) -> TouchZone.MIDDLE1_MIDDLE1
            else -> triangles(x, y, width, height)
        }
    }

    private fun single() = TouchZone1D.MIDDLE1

    private fun two(value: Float, max: Float) = when {
        value / max < 1 / 2F -> TouchZone1D.FIRST
        else -> TouchZone1D.LAST
    }

    private fun three(value: Float, max: Float) = when {
        value / max < 1 / 3F -> TouchZone1D.FIRST
        value / max > 2 / 3F -> TouchZone1D.LAST
        else -> TouchZone1D.MIDDLE1
    }

    private fun threeFixed(value: Float, max: Float) = when {
        value < FIXED_LINE_WIDTH -> TouchZone1D.FIRST
        value > max - FIXED_LINE_WIDTH -> TouchZone1D.LAST
        else -> TouchZone1D.MIDDLE1
    }

    private fun fourFixed(value: Float, max: Float) = when {
        value < FIXED_LINE_WIDTH -> TouchZone1D.FIRST
        value > max - FIXED_LINE_WIDTH -> TouchZone1D.LAST
        value / max < 2 / 4F -> TouchZone1D.MIDDLE1
        else -> TouchZone1D.MIDDLE2
    }

    private fun squares(column: TouchZone1D, row: TouchZone1D): TouchZone = when {
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

    private fun triangles(x: Float, y: Float, width: Float, height: Float): TouchZone {
        val xpart = x / width
        val ypart = y / height

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

    private fun isCenter(x: Float, y: Float, width: Float, height: Float): Boolean {
        val xpart = x / width
        val ypart = y / height
        return xpart >= 1 / 3F && xpart <= 2 / 3F && ypart >= 1 / 3F && ypart <= 2 / 3F
    }
}