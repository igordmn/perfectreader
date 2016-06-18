package com.dmi.perfectreader.fragment.bookcontrol.entity

import com.dmi.util.lang.clamp

enum class TapZoneConfiguration {
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
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |_ _ _|_ _ _|
     */
    TWO_TOP_BOTTOM_CENTER,

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |     |     |
     * |_ _ _|_ _ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    THREE_TOP_BOTTOM_TWO_CENTER,

    /**
     *  _ _ _ _ _ _
     * |     |     |
     * |_ _ _|_ _ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |     |     |
     * |_ _ _|_ _ _|
     */
    TWO_TOP_BOTTOM_THREE_CENTER,

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    TWO_LEFT_RIGHT_CENTER,

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |_ _|   |_ _|
     * |   |_ _|   |
     * |_ _|   |_ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    THREE_LEFT_RIGHT_TWO_CENTER,

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |   |_ _|   |
     * |_ _|   |_ _|
     * |   |_ _|   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    TWO_LEFT_RIGHT_THREE_CENTER,

    /**
     *  _ _ _ _ _ _
     * | /       \ |
     * |/ \ _ _ / \|
     * |   |   |   |
     * |   |_ _|   |
     * |\ /     \ /|
     * |_\_ _ _ _/_|
     */
    TRIANGLE_SIDE_CENTER_CORNERS,

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
    TRIANGLE_SIDES_CENTER,

    /**
     *  _ _ _ _ _
     * | /     \ |
     * |/ \   / \|
     * |   \ /   |
     * |   / \   |
     * |\ /   \ /|
     * |_\_ _ _/_|
     */
    TRIANGLE_SIDE_CORNERS;

    fun getAt(xPart: Float, yPart: Float): TapZone {
        val x = clamp(xPart, 0F, 1F)
        val y = clamp(yPart, 0F, 1F)

        return when (this) {
            NINE -> when {
                x < 1 / 3f && y < 1 / 3f -> TapZone.TOP_LEFT
                x > 2 / 3f && y < 1 / 3f -> TapZone.TOP_RIGHT
                x < 1 / 3f && y > 2 / 3f -> TapZone.BOTTOM_LEFT
                x > 2 / 3f && y > 2 / 3f -> TapZone.BOTTOM_RIGHT
                y < 1 / 3f -> TapZone.TOP
                y > 2 / 3f -> TapZone.BOTTOM
                x < 1 / 3f -> TapZone.LEFT
                x > 2 / 3f -> TapZone.RIGHT
                else -> TapZone.CENTER
            }

            SINGLE -> TapZone.CENTER

            FOUR -> when {
                x < 1 / 2f && y < 1 / 2f -> TapZone.TOP_LEFT
                x >= 1 / 2f && y < 1 / 2f -> TapZone.TOP_RIGHT
                x < 1 / 2f && y >= 1 / 2f -> TapZone.BOTTOM_LEFT
                else -> TapZone.BOTTOM_RIGHT
            }

            TWO_TOP_BOTTOM_CENTER -> when {
                x < 1 / 2f && y < 1 / 3f -> TapZone.TOP_LEFT
                x >= 1 / 2f && y < 1 / 3f -> TapZone.TOP_RIGHT
                x < 1 / 2f && y > 2 / 3f -> TapZone.BOTTOM_LEFT
                x >= 1 / 2f && y > 2 / 3f -> TapZone.BOTTOM_RIGHT
                x < 1 / 2f -> TapZone.LEFT
                else -> TapZone.RIGHT
            }

            THREE_TOP_BOTTOM_TWO_CENTER -> when {
                x < 1 / 3f && y < 1 / 3f -> TapZone.TOP_LEFT
                x > 2 / 3f && y < 1 / 3f -> TapZone.TOP_RIGHT
                x < 1 / 3f && y > 2 / 3f -> TapZone.BOTTOM_LEFT
                x > 2 / 3f && y > 2 / 3f -> TapZone.BOTTOM_RIGHT
                y < 1 / 3f -> TapZone.TOP
                y > 2 / 3f -> TapZone.BOTTOM
                x < 1 / 2f -> TapZone.LEFT
                else -> TapZone.RIGHT
            }

            TWO_TOP_BOTTOM_THREE_CENTER -> when {
                x < 1 / 2f && y < 1 / 3f -> TapZone.TOP_LEFT
                x > 1 / 2f && y < 1 / 3f -> TapZone.TOP_RIGHT
                x < 1 / 2f && y > 2 / 3f -> TapZone.BOTTOM_LEFT
                x > 1 / 2f && y > 2 / 3f -> TapZone.BOTTOM_RIGHT
                x < 1 / 3f -> TapZone.LEFT
                x > 2 / 3f -> TapZone.RIGHT
                else -> TapZone.CENTER
            }

            TWO_LEFT_RIGHT_CENTER -> when {
                x < 1 / 3f && y < 1 / 2f -> TapZone.TOP_LEFT
                x > 2 / 3f && y < 1 / 2f -> TapZone.TOP_RIGHT
                x < 1 / 3f && y >= 1 / 2f -> TapZone.BOTTOM_LEFT
                x > 2 / 3f && y >= 1 / 2f -> TapZone.BOTTOM_RIGHT
                y < 1 / 2f -> TapZone.TOP
                else -> TapZone.BOTTOM
            }

            THREE_LEFT_RIGHT_TWO_CENTER -> when {
                x < 1 / 3f && y < 1 / 3f -> TapZone.TOP_LEFT
                x > 2 / 3f && y < 1 / 3f -> TapZone.TOP_RIGHT
                x < 1 / 3f && y > 1 / 3f -> TapZone.BOTTOM_LEFT
                x > 2 / 3f && y > 1 / 3f -> TapZone.BOTTOM_RIGHT
                x < 1 / 3f -> TapZone.LEFT
                x > 2 / 3f -> TapZone.RIGHT
                y < 1 / 2f -> TapZone.TOP
                else -> TapZone.BOTTOM
            }

            TWO_LEFT_RIGHT_THREE_CENTER -> when {
                x < 1 / 3f && y < 1 / 2f -> TapZone.TOP_LEFT
                x > 2 / 3f && y < 1 / 2f -> TapZone.TOP_RIGHT
                x < 1 / 3f && y > 1 / 2f -> TapZone.BOTTOM_LEFT
                x > 2 / 3f && y > 1 / 2f -> TapZone.BOTTOM_RIGHT
                y < 1 / 3f -> TapZone.TOP
                y > 2 / 3f -> TapZone.BOTTOM
                else -> TapZone.CENTER
            }

            TRIANGLE_SIDE_CENTER_CORNERS -> {
                val inSquare = x >= 1 / 3f && x <= 2 / 3f && y >= 1 / 3f && y <= 2 / 3f
                val bottomOrLeft = y > x
                val topOrLeft = y < 1 - x

                when {
                    inSquare -> TapZone.CENTER
                    y < -x + 1 / 3f -> TapZone.TOP_LEFT
                    y < x - 2 / 3f -> TapZone.TOP_RIGHT
                    y > x + 2 / 3f -> TapZone.BOTTOM_LEFT
                    y > -x + 5 / 3f -> TapZone.BOTTOM_RIGHT
                    bottomOrLeft && topOrLeft -> TapZone.LEFT
                    !bottomOrLeft && topOrLeft -> TapZone.TOP
                    bottomOrLeft -> TapZone.RIGHT
                    else -> TapZone.BOTTOM
                }
            }

            TRIANGLE_SIDES -> {
                val bottomOrLeft = y > x
                val topOrLeft = y < 1 - x

                when {
                    bottomOrLeft && topOrLeft -> TapZone.LEFT
                    !bottomOrLeft && topOrLeft -> TapZone.TOP
                    bottomOrLeft -> TapZone.RIGHT
                    else -> TapZone.BOTTOM
                }
            }

            TRIANGLE_SIDES_CENTER -> {
                val inCenter = x >= 1 / 3f && x <= 2 / 3f && y >= 1 / 3f && y <= 2 / 3f
                val bottomOrLeft = y > x
                val topOrLeft = y < 1 - x

                when {
                    inCenter -> TapZone.CENTER
                    bottomOrLeft && topOrLeft -> TapZone.LEFT
                    !bottomOrLeft && topOrLeft -> TapZone.TOP
                    bottomOrLeft -> TapZone.RIGHT
                    else -> TapZone.BOTTOM
                }
            }

            TRIANGLE_SIDE_CORNERS -> {
                val bottomOrLeft = x < y
                val topOrLeft = x + y < 1

                when {
                    y < -x + 1 / 3f -> TapZone.TOP_LEFT
                    y < x - 2 / 3f -> TapZone.TOP_RIGHT
                    y > x + 2 / 3f -> TapZone.BOTTOM_LEFT
                    y > -x + 5 / 3f -> TapZone.BOTTOM_RIGHT
                    bottomOrLeft && topOrLeft -> TapZone.LEFT
                    !bottomOrLeft && topOrLeft -> TapZone.TOP
                    bottomOrLeft -> TapZone.RIGHT
                    else -> TapZone.BOTTOM
                }
            }
        }
    }
}