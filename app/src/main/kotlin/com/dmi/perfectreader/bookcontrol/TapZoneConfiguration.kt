package com.dmi.perfectreader.bookcontrol

enum class TapZoneConfiguration {
    /**
     * _ _ _ _ _ _
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    NINE,

    /**
     * _ _ _ _ _ _
     * |           |
     * |           |
     * |           |
     * |           |
     * |           |
     * |_ _ _ _ _ _|
     */
    SINGLE,

    /**
     * _ _ _ _ _ _
     * |     |     |
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |     |     |
     * |_ _ _|_ _ _|
     */
    FOUR,

    /**
     * _ _ _ _ _ _
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |_ _ _|_ _ _|
     */
    TWO_TOP_BOTTOM_CENTER,

    /**
     * _ _ _ _ _ _
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |     |     |
     * |_ _ _|_ _ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    THREE_TOP_BOTTOM_TWO_CENTER,

    /**
     * _ _ _ _ _ _
     * |     |     |
     * |_ _ _|_ _ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |     |     |
     * |_ _ _|_ _ _|
     */
    TWO_TOP_BOTTOM_THREE_CENTER,

    /**
     * _ _ _ _ _ _
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    TWO_LEFT_RIGHT_CENTER,

    /**
     * _ _ _ _ _ _
     * |   |   |   |
     * |_ _|   |_ _|
     * |   |_ _|   |
     * |_ _|   |_ _|
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    THREE_LEFT_RIGHT_TWO_CENTER,

    /**
     * _ _ _ _ _ _
     * |   |   |   |
     * |   |_ _|   |
     * |_ _|   |_ _|
     * |   |_ _|   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    TWO_LEFT_RIGHT_THREE_CENTER,

    /**
     * _ _ _ _ _ _
     * | /       \ |
     * |/ \ _ _ / \|
     * |   |   |   |
     * |   |_ _|   |
     * |\ /     \ /|
     * |_\_ _ _ _/_|
     */
    TRIANGLE_SIDE_CENTER_CORNERS,

    /**
     * _ _ _ _
     * |\     /|
     * | \   / |
     * |  \ /  |
     * |  / \  |
     * | /   \ |
     * |/_ _ _\|
     */
    TRIANGLE_SIDES,

    /**
     * _ _ _ _ _
     * |\       /|
     * | \ _ _ / |
     * |  |   |  |
     * |  |_ _|  |
     * | /     \ |
     * |/_ _ _ _\|
     */
    TRIANGLE_SIDES_CENTER,

    /**
     * _ _ _ _ _
     * | /     \ |
     * |/ \   / \|
     * |   \ /   |
     * |   / \   |
     * |\ /   \ /|
     * |_\_ _ _/_|
     */
    TRIANGLE_SIDE_CORNERS;

    fun getAt(xPart: Float, yPart: Float): TapZone {
        var xPart = xPart
        var yPart = yPart
        if (xPart < 0) xPart = 0f
        if (yPart < 0) yPart = 0f
        if (xPart > 1) xPart = 1f
        if (yPart > 1) yPart = 1f

        when (this) {
            NINE -> {
                if (xPart < 1 / 3f && yPart < 1 / 3f) {
                    return TapZone.TOP_LEFT
                } else if (xPart > 2 / 3f && yPart < 1 / 3f) {
                    return TapZone.TOP_RIGHT
                } else if (xPart < 1 / 3f && yPart > 2 / 3f) {
                    return TapZone.BOTTOM_LEFT
                } else if (xPart > 2 / 3f && yPart > 2 / 3f) {
                    return TapZone.BOTTOM_RIGHT
                } else if (yPart < 1 / 3f) {
                    return TapZone.TOP
                } else if (yPart > 2 / 3f) {
                    return TapZone.BOTTOM
                } else if (xPart < 1 / 3f) {
                    return TapZone.LEFT
                } else if (xPart > 2 / 3f) {
                    return TapZone.RIGHT
                } else {
                    return TapZone.CENTER
                }
            }

            SINGLE -> {
                return TapZone.CENTER
            }

            FOUR -> {
                if (xPart < 1 / 2f && yPart < 1 / 2f) {
                    return TapZone.TOP_LEFT
                } else if (xPart >= 1 / 2f && yPart < 1 / 2f) {
                    return TapZone.TOP_RIGHT
                } else if (xPart < 1 / 2f && yPart >= 1 / 2f) {
                    return TapZone.BOTTOM_LEFT
                } else {
                    return TapZone.BOTTOM_RIGHT
                }
            }

            TWO_TOP_BOTTOM_CENTER -> {
                if (xPart < 1 / 2f && yPart < 1 / 3f) {
                    return TapZone.TOP_LEFT
                } else if (xPart >= 1 / 2f && yPart < 1 / 3f) {
                    return TapZone.TOP_RIGHT
                } else if (xPart < 1 / 2f && yPart > 2 / 3f) {
                    return TapZone.BOTTOM_LEFT
                } else if (xPart >= 1 / 2f && yPart > 2 / 3f) {
                    return TapZone.BOTTOM_RIGHT
                } else if (xPart < 1 / 2f) {
                    return TapZone.LEFT
                } else {
                    return TapZone.RIGHT
                }
            }

            THREE_TOP_BOTTOM_TWO_CENTER -> {
                if (xPart < 1 / 3f && yPart < 1 / 3f) {
                    return TapZone.TOP_LEFT
                } else if (xPart > 2 / 3f && yPart < 1 / 3f) {
                    return TapZone.TOP_RIGHT
                } else if (xPart < 1 / 3f && yPart > 2 / 3f) {
                    return TapZone.BOTTOM_LEFT
                } else if (xPart > 2 / 3f && yPart > 2 / 3f) {
                    return TapZone.BOTTOM_RIGHT
                } else if (yPart < 1 / 3f) {
                    return TapZone.TOP
                } else if (yPart > 2 / 3f) {
                    return TapZone.BOTTOM
                } else if (xPart < 1 / 2f) {
                    return TapZone.LEFT
                } else {
                    return TapZone.RIGHT
                }
            }

            TWO_TOP_BOTTOM_THREE_CENTER -> {
                if (xPart < 1 / 2f && yPart < 1 / 3f) {
                    return TapZone.TOP_LEFT
                } else if (xPart > 1 / 2f && yPart < 1 / 3f) {
                    return TapZone.TOP_RIGHT
                } else if (xPart < 1 / 2f && yPart > 2 / 3f) {
                    return TapZone.BOTTOM_LEFT
                } else if (xPart > 1 / 2f && yPart > 2 / 3f) {
                    return TapZone.BOTTOM_RIGHT
                } else if (xPart < 1 / 3f) {
                    return TapZone.LEFT
                } else if (xPart > 2 / 3f) {
                    return TapZone.RIGHT
                } else {
                    return TapZone.CENTER
                }
            }

            TWO_LEFT_RIGHT_CENTER -> {
                if (xPart < 1 / 3f && yPart < 1 / 2f) {
                    return TapZone.TOP_LEFT
                } else if (xPart > 2 / 3f && yPart < 1 / 2f) {
                    return TapZone.TOP_RIGHT
                } else if (xPart < 1 / 3f && yPart >= 1 / 2f) {
                    return TapZone.BOTTOM_LEFT
                } else if (xPart > 2 / 3f && yPart >= 1 / 2f) {
                    return TapZone.BOTTOM_RIGHT
                } else if (yPart < 1 / 2f) {
                    return TapZone.TOP
                } else {
                    return TapZone.BOTTOM
                }
            }

            THREE_LEFT_RIGHT_TWO_CENTER -> {
                if (xPart < 1 / 3f && yPart < 1 / 3f) {
                    return TapZone.TOP_LEFT
                } else if (xPart > 2 / 3f && yPart < 1 / 3f) {
                    return TapZone.TOP_RIGHT
                } else if (xPart < 1 / 3f && yPart > 1 / 3f) {
                    return TapZone.BOTTOM_LEFT
                } else if (xPart > 2 / 3f && yPart > 1 / 3f) {
                    return TapZone.BOTTOM_RIGHT
                } else if (xPart < 1 / 3f) {
                    return TapZone.LEFT
                } else if (xPart > 2 / 3f) {
                    return TapZone.RIGHT
                } else if (yPart < 1 / 2f) {
                    return TapZone.TOP
                } else {
                    return TapZone.BOTTOM
                }
            }

            TWO_LEFT_RIGHT_THREE_CENTER -> {
                if (xPart < 1 / 3f && yPart < 1 / 2f) {
                    return TapZone.TOP_LEFT
                } else if (xPart > 2 / 3f && yPart < 1 / 2f) {
                    return TapZone.TOP_RIGHT
                } else if (xPart < 1 / 3f && yPart > 1 / 2f) {
                    return TapZone.BOTTOM_LEFT
                } else if (xPart > 2 / 3f && yPart > 1 / 2f) {
                    return TapZone.BOTTOM_RIGHT
                } else if (yPart < 1 / 3f) {
                    return TapZone.TOP
                } else if (yPart > 2 / 3f) {
                    return TapZone.BOTTOM
                } else {
                    return TapZone.CENTER
                }
            }

            TRIANGLE_SIDE_CENTER_CORNERS -> {
                val inSquare = xPart >= 1 / 3f && xPart <= 2 / 3f && yPart >= 1 / 3f && yPart <= 2 / 3f
                val bottomOrLeft = yPart > xPart
                val topOrLeft = yPart < 1 - xPart
                if (inSquare) {
                    return TapZone.CENTER
                } else if (yPart < -xPart + 1 / 3f) {
                    return TapZone.TOP_LEFT
                } else if (yPart < xPart - 2 / 3f) {
                    return TapZone.TOP_RIGHT
                } else if (yPart > xPart + 2 / 3f) {
                    return TapZone.BOTTOM_LEFT
                } else if (yPart > -xPart + 5 / 3f) {
                    return TapZone.BOTTOM_RIGHT
                } else if (bottomOrLeft && topOrLeft) {
                    return TapZone.LEFT
                } else if (!bottomOrLeft && topOrLeft) {
                    return TapZone.TOP
                } else if (bottomOrLeft) {
                    return TapZone.RIGHT
                } else {
                    return TapZone.BOTTOM
                }
            }

            TRIANGLE_SIDES -> {
                val bottomOrLeft = yPart > xPart
                val topOrLeft = yPart < 1 - xPart
                if (bottomOrLeft && topOrLeft) {
                    return TapZone.LEFT
                } else if (!bottomOrLeft && topOrLeft) {
                    return TapZone.TOP
                } else if (bottomOrLeft) {
                    return TapZone.RIGHT
                } else {
                    return TapZone.BOTTOM
                }
            }

            TRIANGLE_SIDES_CENTER -> {
                val inCenter = xPart >= 1 / 3f && xPart <= 2 / 3f && yPart >= 1 / 3f && yPart <= 2 / 3f
                val bottomOrLeft = yPart > xPart
                val topOrLeft = yPart < 1 - xPart
                if (inCenter) {
                    return TapZone.CENTER
                } else if (bottomOrLeft && topOrLeft) {
                    return TapZone.LEFT
                } else if (!bottomOrLeft && topOrLeft) {
                    return TapZone.TOP
                } else if (bottomOrLeft) {
                    return TapZone.RIGHT
                } else {
                    return TapZone.BOTTOM
                }
            }

            TRIANGLE_SIDE_CORNERS -> {
                val bottomOrLeft = xPart < yPart
                val topOrLeft = xPart + yPart < 1
                if (yPart < -xPart + 1 / 3f) {
                    return TapZone.TOP_LEFT
                } else if (yPart < xPart - 2 / 3f) {
                    return TapZone.TOP_RIGHT
                } else if (yPart > xPart + 2 / 3f) {
                    return TapZone.BOTTOM_LEFT
                } else if (yPart > -xPart + 5 / 3f) {
                    return TapZone.BOTTOM_RIGHT
                } else if (bottomOrLeft && topOrLeft) {
                    return TapZone.LEFT
                } else if (!bottomOrLeft && topOrLeft) {
                    return TapZone.TOP
                } else if (bottomOrLeft) {
                    return TapZone.RIGHT
                } else {
                    return TapZone.BOTTOM
                }
            }

            else -> throw IllegalStateException()
        }
    }
}