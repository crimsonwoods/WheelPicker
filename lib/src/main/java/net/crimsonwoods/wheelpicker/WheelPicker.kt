package net.crimsonwoods.wheelpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.doOnNextLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

/**
 * Customizable Picker view based on RecyclerView having drum roll like looks.
 * User can scroll up/down items.
 * A selected item is snapped to center position.
 */
open class WheelPicker : RecyclerView {
    private companion object {
        private const val DEFAULT_SHADER_COLOR = Color.BLACK
        private const val DEFAULT_WHEEL_ITEM_COUNT = 5
        private const val DEFAULT_FADE_STRENGTH = 0.9f
    }

    private val snapHelper: SnapHelper = LinearSnapHelper()
    private val selectedPositionChangeListeners: MutableList<OnSelectedPositionChangeListener> =
        mutableListOf()

    private var wheelItemCount = DEFAULT_WHEEL_ITEM_COUNT
    private var solidColorInt: Int = DEFAULT_SHADER_COLOR

    /**
     * Get or set an instance of [FadingEdgeDrawer].
     * By default, an instance of [DefaultFadingEdgeDrawer] is set.
     */
    @get:MainThread
    @set:MainThread
    var fadingEdgeDrawer: FadingEdgeDrawer = DefaultFadingEdgeDrawer()
        get() {
            assertMainThread()
            return field
        }
        set(value) {
            assertMainThread()
            field = value
            invalidate()
        }

    /**
     * Get or set an instance of [ItemTransformer].
     * By default, an instance of [ScalingItemTransformer] is set.
     */
    @get:MainThread
    @set:MainThread
    var itemTransformer: ItemTransformer = ScalingItemTransformer()
        get() {
            assertMainThread()
            return field
        }
        set(value) {
            assertMainThread()
            field = value
            requestLayout()
        }

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        androidx.recyclerview.R.attr.recyclerViewStyle
    ) {
        initialize(attrs, defStyleAttr)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val itemHeight = itemHeight()

        when (MeasureSpec.getMode(heightSpec)) {
            MeasureSpec.AT_MOST -> {
                // height is "wrap_content"
                val heightSpecOverride = if (itemHeight != null) {
                    MeasureSpec.makeMeasureSpec(
                        itemHeight * wheelItemCount,
                        MeasureSpec.EXACTLY
                    )
                } else {
                    heightSpec
                }
                super.onMeasure(widthSpec, heightSpecOverride)
            }
            else -> {
                super.onMeasure(widthSpec, heightSpec)
            }
        }

        if (itemHeight != null) {
            val padding = (measuredHeight - itemHeight) / 2
            if (paddingTop != padding) {
                updatePadding(top = padding, bottom = padding)
                val lm = layoutManager ?: return
                val sv = snapHelper.findSnapView(lm)
                val p = lm.getPosition(checkNotNull(sv))
                if (p != 0) {
                    lm.scrollToPosition(0)
                }
            }
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val layoutManager = layoutManager ?: return
        val snapped = snapHelper.findSnapView(layoutManager) ?: return
        val snappedPosition = layoutManager.getPosition(snapped)

        (0 until layoutManager.childCount)
            .mapNotNull { layoutManager.getChildAt(it) }
            .forEach { itemView ->
                val position = layoutManager.getPosition(itemView)
                itemTransformer.transform(itemView, position, snappedPosition)
            }

        val itemId = adapter?.getItemId(snappedPosition) ?: -1
        dispatchOnSelectedPositionChange(snappedPosition, itemId)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)

        val height = layoutParams?.height ?: -1

        if (height != LayoutParams.WRAP_CONTENT) {
            doOnNextLayout {
                val itemHeight = itemHeight() ?: return@doOnNextLayout
                val padding = (height - itemHeight) / 2
                updatePadding(top = padding, bottom = padding)
                post {
                    layoutManager?.scrollToPosition(0)
                }
            }
        }
    }

    @MainThread
    @ColorInt
    override fun getSolidColor(): Int {
        assertMainThread()
        return solidColorInt
    }

    override fun draw(c: Canvas) {
        super.draw(c)

        val itemHeight = itemHeight() ?: return

        val fadeHeight = (height - itemHeight) / 2f
        val length = if (top + fadeHeight > bottom - fadeHeight) {
            (bottom - top) / 2f
        } else {
            fadeHeight
        }

        val saveCount = c.saveCount

        fadingEdgeDrawer.draw(
            view = this,
            canvas = c,
            fadeHeight = fadeHeight,
            fadeStrength = DEFAULT_FADE_STRENGTH,
            fadeLength = length
        )

        c.restoreToCount(saveCount)
    }

    fun addOnSelectedPositionChangeListener(listener: OnSelectedPositionChangeListener) {
        synchronized(selectedPositionChangeListeners) {
            selectedPositionChangeListeners.add(listener)
        }
    }

    fun removeOnSelectedPositionChangeListener(listener: OnSelectedPositionChangeListener) {
        synchronized(selectedPositionChangeListeners) {
            selectedPositionChangeListeners.remove(listener)
        }
    }

    fun scrollToPosition(position: Int, animation: Boolean) {
        val snapped = snapHelper.findSnapView(layoutManager) ?: return
        val current = layoutManager?.getPosition(snapped) ?: return
        if (current == -1 || current == position) {
            return
        }

        if (animation) {
            smoothScrollToPosition(position)
        } else {
            scrollToPosition(position)
        }
    }

    private fun initialize(attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) {
        clipToPadding = false
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        overScrollMode = OVER_SCROLL_NEVER

        context.obtainStyledAttributes(
            attrs,
            R.styleable.WheelPicker,
            defStyleAttr,
            0
        ).use {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveAttributeDataForStyleable(
                    context,
                    R.styleable.WheelPicker,
                    attrs,
                    it,
                    defStyleAttr,
                    0
                )
            }

            val defaultShaderColor = resolveColor(
                android.R.attr.colorBackground,
                DEFAULT_SHADER_COLOR
            )
            val shaderColor = it.getColor(
                R.styleable.WheelPicker_fadingEdgeColor,
                defaultShaderColor
            )
            solidColorInt = shaderColor
            (fadingEdgeDrawer as DefaultFadingEdgeDrawer).setColor(shaderColor)

            wheelItemCount = it.getInt(
                R.styleable.WheelPicker_wheelSize,
                DEFAULT_WHEEL_ITEM_COUNT
            )
        }

        snapHelper.attachToRecyclerView(this@WheelPicker)
    }

    @ColorInt
    private fun TypedValue.toColor(): Int? {
        return if (type in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT) {
            when (type) {
                TypedValue.TYPE_INT_COLOR_ARGB4 -> {
                    val alpha = data and 0xf000
                    val red = data and 0x0f00
                    val green = data and 0x00f0
                    val blue = data and 0x000f
                    Color.argb(alpha shl 4, red shl 4, green shl 4, blue shl 4)
                }
                TypedValue.TYPE_INT_COLOR_ARGB8 -> {
                    val alpha = Color.alpha(data)
                    val red = Color.red(data)
                    val green = Color.green(data)
                    val blue = Color.blue(data)
                    Color.argb(alpha, red, green, blue)
                }
                TypedValue.TYPE_INT_COLOR_RGB4 -> {
                    val red = data and 0x0f00
                    val green = data and 0x00f0
                    val blue = data and 0x000f
                    Color.argb(255, red shl 4, green shl 4, blue shl 4)
                }
                TypedValue.TYPE_INT_COLOR_RGB8 -> {
                    val red = Color.red(data)
                    val green = Color.green(data)
                    val blue = Color.blue(data)
                    Color.argb(255, red, green, blue)
                }
                else -> throw UnsupportedOperationException("Unknown color type ($type) is detected.")
            }
        } else if (type == TypedValue.TYPE_STRING && data != 0) {
            if (resources.getResourceTypeName(resourceId) == "color") {
                ContextCompat.getColor(context, resourceId)
            } else {
                null
            }
        } else {
            null
        }
    }

    @ColorInt
    private fun resolveColor(@AttrRes id: Int, @ColorInt defVal: Int): Int {
        val value = TypedValue()
        return if (context.theme.resolveAttribute(id, value, true)) {
            value.toColor()
        } else {
            null
        } ?: defVal
    }

    private fun assertMainThread() {
        assertMainThread {
            "Should call on main thread."
        }
    }

    private fun assertMainThread(message: () -> Any) {
        check(context.mainLooper.isCurrentThread, lazyMessage = message)
    }

    private fun dispatchOnSelectedPositionChange(position: Int, itemId: Long) {
        val listeners = synchronized(selectedPositionChangeListeners) {
            selectedPositionChangeListeners.toList()
        }
        listeners.onEach {
            it.onChange(position = position, itemId = itemId)
        }
    }

    private fun itemHeight(): Int? {
        val layoutManager = layoutManager ?: return null
        val children = (0 until layoutManager.childCount).map {
            checkNotNull(layoutManager.getChildAt(it))
        }
        return children.maxOfOrNull { child ->
            layoutManager.measureChildWithMargins(
                child,
                0,
                0
            )
            layoutManager.getDecoratedMeasuredHeight(child) + child.marginTop + child.marginBottom
        }
    }

    fun interface OnSelectedPositionChangeListener {
        fun onChange(position: Int, itemId: Long)
    }
}
