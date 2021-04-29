package net.crimsonwoods.wheelpicker

import android.view.View
import kotlin.math.abs

/**
 * Default implementation of [ItemTransformer].
 * This transformer scales down the view size.
 */
class ScalingItemTransformer(
    private val scalingCoefficient: Float = SCALING_COEFFICIENT
) : ItemTransformer {
    override fun transform(view: View, position: Int, centerPosition: Int) {
        val diff = abs(centerPosition - position)
        view.scaleX = 1.0f - diff * scalingCoefficient
        view.scaleY = 1.0f - diff * scalingCoefficient
    }

    companion object {
        private const val SCALING_COEFFICIENT = 0.2f
    }
}
