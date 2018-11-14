package com.dmi.util.action

enum class TouchZone1D { FIRST, MIDDLE1, MIDDLE2, LAST }

enum class TouchZone {
    TOP_LEFT, TOP_MIDDLE1, TOP_MIDDLE2, TOP_RIGHT,
    MIDDLE1_LEFT, MIDDLE1_MIDDLE1, MIDDLE1_MIDDLE2, MIDDLE1_RIGHT,
    MIDDLE2_LEFT, MIDDLE2_MIDDLE1, MIDDLE2_MIDDLE2, MIDDLE2_RIGHT,
    BOTTOM_LEFT, BOTTOM_MIDDLE1, BOTTOM_MIDDLE2, BOTTOM_RIGHT
}

fun zone(column: TouchZone1D, row: TouchZone1D): TouchZone = when {
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