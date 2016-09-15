package com.dmi.util.graphic

import java.lang.Math.sqrt

fun distance(x1: Float, y1: Float, x2: Float, y2: Float) = sqrt(sqrDistance(x1, y1, x2, y2).toDouble()).toFloat()
fun sqrDistance(x1: Float, y1: Float, x2: Float, y2: Float) = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)