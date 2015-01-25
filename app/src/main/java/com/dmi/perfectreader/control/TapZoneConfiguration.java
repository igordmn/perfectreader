package com.dmi.perfectreader.control;

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
    NINE_SQUARES,

    /**
     *  _ _ _ _ _ _
     * |     |     |
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |     |     |
     * |_ _ _|_ _ _|
     */
    FOUR_SQUARES,

    /**
     *  _ _ _ _ _ _
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |_ _ _|_ _ _|
     * |     |     |
     * |_ _ _|_ _ _|
     */
    THREE_SQUARES_ON_LEFT_AND_RIGHT,

    /**
     *  _ _ _ _ _ _
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     * |   |   |   |
     * |   |   |   |
     * |_ _|_ _|_ _|
     */
    THREE_SQUARES_ON_TOP_AND_BOTTOM,

    /**
     *  _ _ _ _
     * |\     /|
     * | \   / |
     * |  \ /  |
     * |  / \  |
     * | /   \ |
     * |/_ _ _\|
     */
    FOUR_TRIANGLES,

    /**
     *  _ _ _ _ _
     * |\       /|
     * | \ _ _ / |
     * |  |   |  |
     * |  |_ _|  |
     * | /     \ |
     * |/_ _ _ _\|
     */
    FOUR_TRAPEZOIDS_AND_SQUARE;

    public TapZone getAt(float xPart, float yPart) {
        if (xPart < 0) xPart = 0;
        if (yPart < 0) yPart = 0;
        if (xPart > 1) xPart = 1;
        if (yPart > 1) yPart = 1;

        switch (this) {
            case NINE_SQUARES: {
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

            case FOUR_SQUARES: {
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

            case THREE_SQUARES_ON_LEFT_AND_RIGHT: {
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

            case THREE_SQUARES_ON_TOP_AND_BOTTOM: {
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

            case FOUR_TRIANGLES: {
                boolean bottomOrLeft = xPart < yPart;
                boolean topOrLeft = xPart + yPart < 1;
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

            case FOUR_TRAPEZOIDS_AND_SQUARE: {
                boolean inSquare = xPart >= 1 / 3F && xPart <= 2 / 3F && yPart >= 1 / 3F && yPart <= 2 / 3F;
                boolean bottomOrLeft = xPart < yPart;
                boolean topOrLeft = xPart + yPart < 1;
                if (inSquare) {
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

            default:
                throw new IllegalStateException();
        }
    }
}
