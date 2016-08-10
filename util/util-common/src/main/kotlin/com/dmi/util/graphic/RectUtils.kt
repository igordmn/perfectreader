package com.dmi.util.graphic

import java.lang.Math.sqrt

fun distanceToRect(position: PositionF, rect: RectF): Float =
        distanceToRect(position.x, position.y, rect.left, rect.top, rect.right, rect.bottom)

fun distanceToRect(x: Float, y: Float, rectLeft: Float, rectTop: Float, rectRight: Float, rectBottom: Float): Float =
        sqrt(sqrDistanceToRect(x, y, rectLeft, rectTop, rectRight, rectBottom).toDouble()).toFloat()

fun sqrDistanceToRect(x: Float, y: Float, rectLeft: Float, rectTop: Float, rectRight: Float, rectBottom: Float): Float =
    if (x < rectLeft) {
        if (y < rectTop) {
            sqrDistance(x, y, rectLeft, rectTop)
        } else if (y > rectBottom) {
            sqrDistance(x, y, rectLeft, rectBottom)
        } else {
            (rectLeft - x) * (rectLeft - x)
        }
    } else if (x > rectRight) {
        if (y < rectTop) {
            sqrDistance(x, y, rectRight, rectTop)
        } else if (y > rectBottom) {
            sqrDistance(x, y, rectRight, rectBottom)
        } else {
            (x - rectRight) * (x - rectRight)
        }
    } else {
        if (y < rectTop) {
            (rectTop - y) * (rectTop - y)
        } else if (y > rectBottom) {
            (y - rectBottom) * (y - rectBottom)
        } else {
            0F
        }
    }