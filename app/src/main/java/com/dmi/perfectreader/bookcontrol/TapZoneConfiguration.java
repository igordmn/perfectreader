package com.dmi.perfectreader.bookcontrol;

public enum TapZoneConfiguration {
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

    public TapZone getAt(float xPart, float yPart) {
        if (xPart < 0) xPart = 0;
        if (yPart < 0) yPart = 0;
        if (xPart > 1) xPart = 1;
        if (yPart > 1) yPart = 1;

        switch (this) {
            case NINE: {
                if (xPart < 1 / 3F && yPart < 1 / 3F) {
                    return TapZone.TOP_LEFT;
                } else if (xPart > 2 / 3F && yPart < 1 / 3F) {
                    return TapZone.TOP_RIGHT;
                } else if (xPart < 1 / 3F && yPart > 2 / 3F) {
                    return TapZone.BOTTOM_LEFT;
                } else if (xPart > 2 / 3F && yPart > 2 / 3F) {
                    return TapZone.BOTTOM_RIGHT;
                } else if (yPart < 1 / 3F) {
                    return TapZone.TOP;
                } else if (yPart > 2 / 3F) {
                    return TapZone.BOTTOM;
                } else if (xPart < 1 / 3F) {
                    return TapZone.LEFT;
                } else if (xPart > 2 / 3F) {
                    return TapZone.RIGHT;
                } else {
                    return TapZone.CENTER;
                }
            }

            case SINGLE: {
                return TapZone.CENTER;
            }

            case FOUR: {
                if (xPart < 1 / 2F && yPart < 1 / 2F) {
                    return TapZone.TOP_LEFT;
                } else if (xPart >= 1 / 2F && yPart < 1 / 2F) {
                    return TapZone.TOP_RIGHT;
                } else if (xPart < 1 / 2F && yPart >= 1 / 2F) {
                    return TapZone.BOTTOM_LEFT;
                } else {
                    return TapZone.BOTTOM_RIGHT;
                }
            }

            case TWO_TOP_BOTTOM_CENTER: {
                if (xPart < 1 / 2F && yPart < 1 / 3F) {
                    return TapZone.TOP_LEFT;
                } else if (xPart >= 1 / 2F && yPart < 1 / 3F) {
                    return TapZone.TOP_RIGHT;
                } else if (xPart < 1 / 2F && yPart > 2 / 3F) {
                    return TapZone.BOTTOM_LEFT;
                } else if (xPart >= 1 / 2F && yPart > 2 / 3F) {
                    return TapZone.BOTTOM_RIGHT;
                } else if (xPart < 1 / 2F) {
                    return TapZone.LEFT;
                } else {
                    return TapZone.RIGHT;
                }
            }

            case THREE_TOP_BOTTOM_TWO_CENTER: {
                if (xPart < 1 / 3F && yPart < 1 / 3F) {
                    return TapZone.TOP_LEFT;
                } else if (xPart > 2 / 3F && yPart < 1 / 3F) {
                    return TapZone.TOP_RIGHT;
                } else if (xPart < 1 / 3F && yPart > 2 / 3F) {
                    return TapZone.BOTTOM_LEFT;
                } else if (xPart > 2 / 3F && yPart > 2 / 3F) {
                    return TapZone.BOTTOM_RIGHT;
                } else if (yPart < 1 / 3F) {
                    return TapZone.TOP;
                } else if (yPart > 2 / 3F) {
                    return TapZone.BOTTOM;
                } else if (xPart < 1 / 2F) {
                    return TapZone.LEFT;
                } else {
                    return TapZone.RIGHT;
                }
            }

            case TWO_TOP_BOTTOM_THREE_CENTER: {
                if (xPart < 1 / 2F && yPart < 1 / 3F) {
                    return TapZone.TOP_LEFT;
                } else if (xPart > 1 / 2F && yPart < 1 / 3F) {
                    return TapZone.TOP_RIGHT;
                } else if (xPart < 1 / 2F && yPart > 2 / 3F) {
                    return TapZone.BOTTOM_LEFT;
                } else if (xPart > 1 / 2F && yPart > 2 / 3F) {
                    return TapZone.BOTTOM_RIGHT;
                } else if (xPart < 1 / 3F) {
                    return TapZone.LEFT;
                } else if (xPart > 2 / 3F) {
                    return TapZone.RIGHT;
                } else {
                    return TapZone.CENTER;
                }
            }

            case TWO_LEFT_RIGHT_CENTER: {
                if (xPart < 1 / 3F && yPart < 1 / 2F) {
                    return TapZone.TOP_LEFT;
                } else if (xPart > 2 / 3F && yPart < 1 / 2F) {
                    return TapZone.TOP_RIGHT;
                } else if (xPart < 1 / 3F && yPart >= 1 / 2F) {
                    return TapZone.BOTTOM_LEFT;
                } else if (xPart > 2 / 3F && yPart >= 1 / 2F) {
                    return TapZone.BOTTOM_RIGHT;
                } else if (yPart < 1 / 2F) {
                    return TapZone.TOP;
                } else {
                    return TapZone.BOTTOM;
                }
            }

            case THREE_LEFT_RIGHT_TWO_CENTER: {
                if (xPart < 1 / 3F && yPart < 1 / 3F) {
                    return TapZone.TOP_LEFT;
                } else if (xPart > 2 / 3F && yPart < 1 / 3F) {
                    return TapZone.TOP_RIGHT;
                } else if (xPart < 1 / 3F && yPart > 1 / 3F) {
                    return TapZone.BOTTOM_LEFT;
                } else if (xPart > 2 / 3F && yPart > 1 / 3F) {
                    return TapZone.BOTTOM_RIGHT;
                } else if (xPart < 1 / 3F) {
                    return TapZone.LEFT;
                } else if (xPart > 2 / 3F) {
                    return TapZone.RIGHT;
                } else if (yPart < 1 / 2F) {
                    return TapZone.TOP;
                } else {
                    return TapZone.BOTTOM;
                }
            }

            case TWO_LEFT_RIGHT_THREE_CENTER: {
                if (xPart < 1 / 3F && yPart < 1 / 2F) {
                    return TapZone.TOP_LEFT;
                } else if (xPart > 2 / 3F && yPart < 1 / 2F) {
                    return TapZone.TOP_RIGHT;
                } else if (xPart < 1 / 3F && yPart > 1 / 2F) {
                    return TapZone.BOTTOM_LEFT;
                } else if (xPart > 2 / 3F && yPart > 1 / 2F) {
                    return TapZone.BOTTOM_RIGHT;
                } else if (yPart < 1 / 3F) {
                    return TapZone.TOP;
                } else if (yPart > 2 / 3F) {
                    return TapZone.BOTTOM;
                } else {
                    return TapZone.CENTER;
                }
            }

            case TRIANGLE_SIDE_CENTER_CORNERS: {
                boolean inSquare = xPart >= 1 / 3F && xPart <= 2 / 3F && yPart >= 1 / 3F && yPart <= 2 / 3F;
                boolean bottomOrLeft = yPart > xPart;
                boolean topOrLeft = yPart < 1 - xPart;
                if (inSquare) {
                    return TapZone.CENTER;
                } else if (yPart < -xPart + 1 / 3F) {
                    return TapZone.TOP_LEFT;
                } else if (yPart < xPart - 2 / 3F) {
                    return TapZone.TOP_RIGHT;
                } else if (yPart > xPart + 2 / 3F) {
                    return TapZone.BOTTOM_LEFT;
                } else if (yPart > -xPart + 5 / 3F) {
                    return TapZone.BOTTOM_RIGHT;
                }  else if (bottomOrLeft && topOrLeft) {
                    return TapZone.LEFT;
                } else if (!bottomOrLeft && topOrLeft) {
                    return TapZone.TOP;
                } else if (bottomOrLeft) {
                    return TapZone.RIGHT;
                } else {
                    return TapZone.BOTTOM;
                }
            }

            case TRIANGLE_SIDES: {
                boolean bottomOrLeft = yPart > xPart;
                boolean topOrLeft = yPart < 1 - xPart;
                if (bottomOrLeft && topOrLeft) {
                    return TapZone.LEFT;
                } else if (!bottomOrLeft && topOrLeft) {
                    return TapZone.TOP;
                } else if (bottomOrLeft) {
                    return TapZone.RIGHT;
                } else {
                    return TapZone.BOTTOM;
                }
            }

            case TRIANGLE_SIDES_CENTER: {
                boolean inCenter = xPart >= 1 / 3F && xPart <= 2 / 3F && yPart >= 1 / 3F && yPart <= 2 / 3F;
                boolean bottomOrLeft = yPart > xPart;
                boolean topOrLeft = yPart < 1 - xPart;
                if (inCenter) {
                    return TapZone.CENTER;
                } else if (bottomOrLeft && topOrLeft) {
                    return TapZone.LEFT;
                } else if (!bottomOrLeft && topOrLeft) {
                    return TapZone.TOP;
                } else if (bottomOrLeft) {
                    return TapZone.RIGHT;
                } else {
                    return TapZone.BOTTOM;
                }
            }

            case TRIANGLE_SIDE_CORNERS: {
                boolean bottomOrLeft = xPart < yPart;
                boolean topOrLeft = xPart + yPart < 1;
                if (yPart < -xPart + 1 / 3F) {
                    return TapZone.TOP_LEFT;
                } else if (yPart < xPart - 2 / 3F) {
                    return TapZone.TOP_RIGHT;
                } else if (yPart > xPart + 2 / 3F) {
                    return TapZone.BOTTOM_LEFT;
                } else if (yPart > -xPart + 5 / 3F) {
                    return TapZone.BOTTOM_RIGHT;
                } else if (bottomOrLeft && topOrLeft) {
                    return TapZone.LEFT;
                } else if (!bottomOrLeft && topOrLeft) {
                    return TapZone.TOP;
                } else if (bottomOrLeft) {
                    return TapZone.RIGHT;
                } else {
                    return TapZone.BOTTOM;
                }
            }

            default:
                throw new IllegalStateException();
        }
    }
}
