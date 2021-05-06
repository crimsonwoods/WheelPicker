package net.crimsonwoods.wheelpicker

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller

class SlowLinearSmoothScroller(context: Context) : LinearSmoothScroller(context) {
    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
    }

    companion object {
        // LinearSmoothScroller.MILLISECONDS_PER_INCH = 25f
        private const val MILLISECONDS_PER_INCH = 100f
    }
}