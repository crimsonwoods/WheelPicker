package net.crimsonwoods.wheelpicker

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import androidx.annotation.ColorInt

/**
 * Default implementation of [FadingEdgeDrawer].
 * This implementation refers to View's implementation of fading edge.
 */
class DefaultFadingEdgeDrawer : FadingEdgeDrawer {
    private val fadePaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }
    private val fadeMatrix = Matrix()

    private lateinit var fade: Shader

    private var shaderColor: Int = Color.TRANSPARENT

    fun setColor(@ColorInt color: Int) {
        shaderColor = color
        fade = makeShader(color)
        fadePaint.xfermode = makeXferMode(color)
    }

    override fun draw(
        view: WheelPicker,
        canvas: Canvas,
        fadeHeight: Float,
        fadeStrength: Float,
        fadeLength: Float
    ) {
        val left = 0
        val right = left + (view.right - view.left)
        val top = 0
        val bottom = top + (view.bottom - view.top)

        // draw vertical fading edge on bottom
        fadeMatrix.setScale(1f, fadeHeight * fadeStrength)
        fadeMatrix.postRotate(180f)
        fadeMatrix.postTranslate(left.toFloat(), bottom.toFloat())
        fade.setLocalMatrix(fadeMatrix)
        fadePaint.shader = fade
        if (shaderColor != 0) {
            canvas.drawRect(
                left.toFloat(),
                bottom.toFloat() - fadeLength,
                right.toFloat(),
                bottom.toFloat(),
                fadePaint
            )
        }

        // draw vertical fading edge on top
        fadeMatrix.setScale(1f, fadeHeight * fadeStrength)
        fadeMatrix.postTranslate(left.toFloat(), top.toFloat())
        fade.setLocalMatrix(fadeMatrix)
        fadePaint.shader = fade
        if (shaderColor != 0) {
            canvas.drawRect(
                left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                top.toFloat() + fadeLength,
                fadePaint
            )
        }
    }

    companion object {
        private fun makeShader(@ColorInt solidColor: Int): LinearGradient {
            return if (solidColor != 0) {
                LinearGradient(
                    0f, 0f, 0f, 1f,
                    Color.argb(
                        0xff,
                        Color.red(solidColor),
                        Color.green(solidColor),
                        Color.blue(solidColor)
                    ),
                    Color.argb(
                        0,
                        Color.red(solidColor),
                        Color.green(solidColor),
                        Color.blue(solidColor)
                    ),
                    Shader.TileMode.CLAMP
                )
            } else {
                LinearGradient(
                    0f, 0f, 0f, 1f,
                    Color.BLACK,
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
                )
            }
        }

        private fun makeXferMode(@ColorInt solidColor: Int): PorterDuffXfermode? {
            return if (solidColor != 0) {
                null
            } else {
                PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            }
        }
    }
}
