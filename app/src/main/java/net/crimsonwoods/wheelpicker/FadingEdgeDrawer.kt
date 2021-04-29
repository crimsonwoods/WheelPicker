package net.crimsonwoods.wheelpicker

import android.graphics.Canvas

fun interface FadingEdgeDrawer {
    fun draw(
        view: WheelPicker,
        canvas: Canvas,
        fadeHeight: Float,
        fadeStrength: Float,
        fadeLength: Float
    )
}
