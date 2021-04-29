package net.crimsonwoods.wheelpicker

import android.view.View

fun interface ItemTransformer {
    fun transform(view: View, position: Int, centerPosition: Int)
}
