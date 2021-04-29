package net.crimsonwoods.wheelpicker

import android.graphics.Canvas

interface FadingEdgeDrawer {
    fun draw(
        view: WheelPicker,
        canvas: Canvas,
        fadeHeight: Float,
        fadeStrength: Float,
        fadeLength: Float
    )
}
